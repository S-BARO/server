package com.dh.baro.product.domain.service

import com.dh.baro.core.exception.InventoryInsufficientException
import com.dh.baro.product.domain.InventoryItem
import com.dh.baro.product.domain.repository.ProductRepository
import com.dh.baro.product.infra.redis.InventoryRedisRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class InventoryService(
    private val inventoryRedisRepository: InventoryRedisRepository,
    private val productRepository: ProductRepository,
) {

    fun deductStocksFromRedis(items: List<InventoryItem>): Boolean {
       return inventoryRedisRepository.deductStocks(items)
    }

    @Transactional
    fun deductStocksFromDatabase(items: List<InventoryItem>) {
        items.forEach { item ->
            deductStockFromDB(item.productId, item.quantity)
        }
    }

    fun deductStockFromDB(productId: Long, quantity: Int) {
        val updated = productRepository.deductStock(productId, quantity)

        if (updated == 0) {
            throw InventoryInsufficientException(productId)
        }
    }
}
