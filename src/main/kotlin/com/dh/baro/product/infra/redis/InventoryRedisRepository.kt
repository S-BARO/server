package com.dh.baro.product.infra.redis

import com.dh.baro.core.ErrorMessage
import com.dh.baro.core.exception.ConflictException
import com.dh.baro.product.domain.InventoryItem
import com.dh.baro.product.domain.repository.ProductRepository
import org.redisson.api.RedissonClient
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import java.time.Duration
import jakarta.annotation.PostConstruct
import org.redisson.api.RScript

@Component
class InventoryRedisRepository(
    private val redissonClient: RedissonClient,
    private val productRepository: ProductRepository,
) {
    private lateinit var batchStockDeductionScript: String
    private lateinit var batchStockRestoreScript: String
    private lateinit var deductScriptSha: String
    private lateinit var restoreScriptSha: String
    
    @PostConstruct
    private fun loadLuaScripts() {
        batchStockDeductionScript = ClassPathResource("lua/deduct-stocks.lua").inputStream.bufferedReader().readText()
        batchStockRestoreScript = ClassPathResource("lua/restore-stocks.lua").inputStream.bufferedReader().readText()
        
        deductScriptSha = redissonClient.script.scriptLoad(batchStockDeductionScript)
        restoreScriptSha = redissonClient.script.scriptLoad(batchStockRestoreScript)
    }

    fun deductStocks(items: List<InventoryItem>): Boolean {
        if (items.isEmpty()) return true
        
        val mergedItems = mergedItems(items)
        return executeScriptWithRetry(deductScriptSha, mergedItems, initializeOnMissing = true) { result ->
            when (result) {
                INSUFFICIENT_STOCK -> throw ConflictException(ErrorMessage.INSUFFICIENT_STOCK.message)
                else -> false
            }
        }
    }

    fun restoreStocks(items: List<InventoryItem>): Boolean {
        if (items.isEmpty()) return true
        
        val mergedItems = mergedItems(items)
        return executeScriptWithRetry(restoreScriptSha, mergedItems, initializeOnMissing = false) { _ ->
            false
        }
    }

    private fun mergedItems(items: List<InventoryItem>): List<InventoryItem> {
        return items.groupBy { it.productId }
            .map { (productId, itemList) ->
                InventoryItem(productId, itemList.sumOf { it.quantity })
            }
    }
    
    private fun executeScriptWithRetry(
        scriptSha: String,
        items: List<InventoryItem>,
        initializeOnMissing: Boolean,
        shouldThrowError: (Long) -> Boolean
    ): Boolean {
        val keys = items.map { getStockKey(it.productId) }
        val quantities = items.map { it.quantity.toString() }
        
        var retryCount = 0
        while (retryCount < MAX_RETRY_COUNT) {
            val result = redissonClient.script.evalSha<Long>(
                RScript.Mode.READ_WRITE,
                scriptSha,
                RScript.ReturnType.INTEGER,
                keys,
                *quantities.toTypedArray()
            )

            when (result) {
                MISSING_KEYS -> {
                    if (initializeOnMissing) {
                        val productIds = items.map { it.productId }
                        initializeStocksFromDb(productIds)
                        retryCount++
                        continue
                    }
                    return shouldThrowError(result)
                }
                else -> {
                    return shouldThrowError(result)
                }
            }
        }
        
        throw IllegalStateException(ErrorMessage.INVENTORY_RETRY_EXCEEDED.format(MAX_RETRY_COUNT))
    }

    private fun initializeStocksFromDb(productIds: List<Long>) {
        val products = productRepository.findAllById(productIds)

        products.forEach { product ->
            val key = getStockKey(product.id)
            val stock = product.getQuantity()
            val bucket = redissonClient.getBucket<Int>(key)
            bucket.set(stock)
            bucket.expire(Duration.ofHours(12))
        }
    }

    private fun getStockKey(productId: Long): String = "product:stock:$productId"
    
    companion object {
        private const val MAX_RETRY_COUNT = 1
        private const val MISSING_KEYS = -1L
        private const val INSUFFICIENT_STOCK = -2L
    }
}
