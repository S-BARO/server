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
    
    @PostConstruct
    private fun loadLuaScripts() {
        batchStockDeductionScript = ClassPathResource("lua/deduct-stocks.lua").inputStream.bufferedReader().readText()
        batchStockRestoreScript = ClassPathResource("lua/restore-stocks.lua").inputStream.bufferedReader().readText()
    }

    fun deductStocks(items: List<InventoryItem>): Boolean {
        if (items.isEmpty()) return true
        
        val keys = items.map { getStockKey(it.productId) }
        val quantities = items.map { it.quantity.toString() }
        
        val result = redissonClient.script.eval<Long>(
            RScript.Mode.READ_WRITE,
            batchStockDeductionScript,
            RScript.ReturnType.INTEGER,
            keys,
            *quantities.toTypedArray()
        )

        return when (result) {
            -1L -> { // Redis에 재고 정보가 없는 상품들이 있는 경우, DB에서 초기화 후 재시도
                val productIds = items.map { it.productId }
                initializeStocksFromDb(productIds)
                deductStocks(items) // 재귀 호출
            }
            -2L -> {
                throw ConflictException(ErrorMessage.INSUFFICIENT_STOCK.message)
            }
            else -> true
        }
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

    fun restoreStocks(items: List<InventoryItem>): Boolean {
        if (items.isEmpty()) return true
        
        val keys = items.map { getStockKey(it.productId) }
        val quantities = items.map { it.quantity.toString() }
        
        val result = redissonClient.script.eval<Long>(
            RScript.Mode.READ_WRITE,
            batchStockRestoreScript,
            RScript.ReturnType.INTEGER,
            keys,
            *quantities.toTypedArray()
        )
        
        return when (result) {
            1L -> { // Redis에 재고 정보가 없는 상품들이 있는 경우, DB에서 초기화 후 재시도
                val productIds = items.map { it.productId }
                initializeStocksFromDb(productIds)
                restoreStocks(items) // 재귀 호출
            }
            else -> true
        }
    }

    private fun getStockKey(productId: Long): String = "product:stock:$productId"
}
