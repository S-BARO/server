package com.dh.baro.product.infra.redis

import com.dh.baro.core.ErrorMessage
import com.dh.baro.product.domain.InventoryItem
import com.dh.baro.product.domain.repository.ProductRepository
import org.redisson.api.RedissonClient
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import jakarta.annotation.PostConstruct
import org.redisson.api.RScript
import org.redisson.client.codec.StringCodec

@Component
class InventoryRedisRepository(
    private val redissonClient: RedissonClient,
    private val productRepository: ProductRepository,
) {
    private lateinit var batchStockDeductionScript: String
    private lateinit var batchStockRestoreScript: String
    private lateinit var initStocksScript: String

    @PostConstruct
    private fun loadLuaScripts() {
        batchStockDeductionScript = ClassPathResource("lua/deduct-stocks.lua").inputStream.bufferedReader().readText()
        batchStockRestoreScript = ClassPathResource("lua/restore-stocks.lua").inputStream.bufferedReader().readText()
        initStocksScript = ClassPathResource("lua/init-stocks.lua").inputStream.bufferedReader().readText()
    }

    fun deductStocks(items: List<InventoryItem>): Boolean {
        if (items.isEmpty()) return true
        val mergedItems = mergedItems(items)

        return executeScript(batchStockDeductionScript, mergedItems, autoInitializeFromDb = true) { result ->
            when (result) {
                INSUFFICIENT_STOCK -> throw IllegalArgumentException(ErrorMessage.INSUFFICIENT_STOCK.message)
                else -> false
            }
        }
    }

    fun rollbackStocks(items: List<InventoryItem>): Boolean {
        if (items.isEmpty()) return true
        val mergedItems = mergedItems(items)

        return executeScript(batchStockRestoreScript, mergedItems, autoInitializeFromDb = false) { false }
    }

    private fun mergedItems(items: List<InventoryItem>): List<InventoryItem> {
        return items.groupBy { it.productId }
            .map { (productId, itemList) ->
                InventoryItem(productId, itemList.sumOf { it.quantity })
            }
    }

    private fun executeScript(
        script: String,
        items: List<InventoryItem>,
        autoInitializeFromDb: Boolean,
        shouldThrowError: (Long) -> Boolean
    ): Boolean {
        val keys = items.map { getStockKey(it.productId) }
        val quantities = items.map { it.quantity.toString() }

        var retryCount = 0
        while (retryCount < MAX_RETRY_COUNT) {
            val redisScript = redissonClient.getScript(StringCodec.INSTANCE)

            val result = redisScript.eval<Long>(
                RScript.Mode.READ_WRITE,
                script,
                RScript.ReturnType.INTEGER,
                keys,
                *quantities.toTypedArray()
            )

            when (result) {
                MISSING_KEYS -> {
                    if (autoInitializeFromDb) {
                        val productIds = items.map { it.productId }
                        initializeStocksFromDb(productIds)
                        retryCount++
                        continue
                    }
                    return shouldThrowError(result)
                }

                INVALID_AMOUNT -> throw IllegalArgumentException(ErrorMessage.INVALID_STOCK_AMOUNT.message)
                ERROR_WITH_ROLLBACK -> throw IllegalArgumentException("Error with rollback")

                else -> {
                    return shouldThrowError(result)
                }
            }
        }

        throw IllegalStateException(ErrorMessage.INVENTORY_RETRY_EXCEEDED.format(MAX_RETRY_COUNT))
    }

    private fun initializeStocksFromDb(productIds: List<Long>) {
        val products = productRepository.findAllById(productIds)
        
        if (products.isEmpty()) return
        
        val keys = products.map { getStockKey(it.id) }
        val stocks = products.map { it.getQuantity().toString() }
        
        val redisScript = redissonClient.getScript(StringCodec.INSTANCE)
        redisScript.eval<Long>(
            RScript.Mode.READ_WRITE,
            initStocksScript,
            RScript.ReturnType.INTEGER,
            keys,
            *stocks.toTypedArray()
        )
    }

    private fun getStockKey(productId: Long): String = "product:stock:$productId"

    companion object {
        private const val MAX_RETRY_COUNT = 2
        private const val MISSING_KEYS = -1L
        private const val INSUFFICIENT_STOCK = -2L
        private const val INVALID_AMOUNT = -3L
        private const val ERROR_WITH_ROLLBACK = -4L
    }
}
