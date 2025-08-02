package com.dh.baro.product.domain

import com.dh.baro.core.instant
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
@Transactional(readOnly = true)
class ProductQueryService(
    private val productRepository: ProductRepository,
) {

    fun getPopularProducts(categoryId: Long?, cursorId: Long?, size: Int): List<Product> =
        productRepository.findPopularProductsByCursor(categoryId, cursorId, size)

    fun getNewestProducts(categoryId: Long?, cursorId: Long?, size: Int): List<Product> =
        productRepository.findNewestProductsByCursor(
            createdAfter = createdAfter(),
            categoryId = categoryId,
            cursorId = cursorId,
            size = size,
        )

    private fun createdAfter(): Instant =
        instant().minus(NEW_PERIOD_DAYS, ChronoUnit.DAYS)

    fun getProductDetail(productId: Long): Product =
        productRepository.findDetailById(productId)

    companion object {
        private const val NEW_PERIOD_DAYS = 30L
    }
}
