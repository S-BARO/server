package com.dh.baro.product.domain

import com.dh.baro.core.ErrorMessage
import com.dh.baro.core.instant
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
@Transactional(readOnly = true)
class ProductQueryService(
    private val productRepository: ProductRepository,
) {

    fun getPopularProducts(
        categoryId: Long?,
        cursorLikes: Int?,
        cursorId: Long?,
        size: Int,
    ): List<Product> {
        val pageable = PageRequest.of(0, size)
        return productRepository.findPopularProductsByCursor(
            categoryId = categoryId,
            cursorLikes = cursorLikes,
            cursorId = cursorId,
            pageable = pageable,
        )
    }

    fun getNewestProducts(
        categoryId: Long?,
        cursorId: Long?,
        size: Int
    ): List<Product> =
        productRepository.findNewestProductsByCursor(
            cutoff = calculateCutoff(),
            categoryId = categoryId,
            cursorId = cursorId,
            size = size,
        )

    private fun calculateCutoff(): Instant =
        instant().minus(NEW_PERIOD_DAYS, ChronoUnit.DAYS)

    fun getProductDetail(productId: Long): Product =
        productRepository.findDetailProductById(productId)
            ?: throw IllegalArgumentException(ErrorMessage.PRODUCT_NOT_FOUND.format(productId))

    companion object {
        private const val NEW_PERIOD_DAYS = 30L
    }
}
