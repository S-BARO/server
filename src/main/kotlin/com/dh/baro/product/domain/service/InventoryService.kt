package com.dh.baro.product.domain.service

import com.dh.baro.core.ErrorMessage
import com.dh.baro.core.exception.ConflictException
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

    /**
     * DB에서 실제 재고 차감 (비동기 이벤트 처리용)
     */
    @Transactional
    fun deductStockFromDB(productId: Long, quantity: Int) {
        val updated = productRepository.deductStock(productId, quantity)

        if (updated == 0) {
            throw ConflictException(ErrorMessage.OUT_OF_STOCK.format(productId))
        }
    }

    /**
     * 여러 상품의 DB 재고를 한번에 차감 (Kafka 이벤트 처리용)
     */
    @Transactional
    fun deductStocksFromDatabase(items: List<InventoryItem>) {
        items.forEach { item ->
            deductStockFromDB(item.productId, item.quantity)
        }
    }
}
