package com.dh.baro.product.domain.service

import com.dh.baro.core.ErrorMessage
import com.dh.baro.core.instant
import com.dh.baro.product.domain.Product
import com.dh.baro.product.domain.repository.ProductRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
@Transactional(readOnly = true)
class ProductQueryService(
    private val productRepository: ProductRepository,
) {
    fun getProductsExists(productIds: List<Long>): List<Product> {
        val distinctIds = productIds.toSet()
        val products = productRepository.findAllById(distinctIds).toList()

        require(products.size == distinctIds.size) {
            ErrorMessage.PRODUCT_NOT_FOUND.format(productIds)
        }

        return products
    }

    fun checkProductsExists(productIds: List<Long>) {
        getProductsExists(productIds)
    }

    fun getProductDetail(productId: Long): Product =
        productRepository.findDetailProductById(productId)
            ?: throw IllegalArgumentException(ErrorMessage.PRODUCT_NOT_FOUND.format(productId))

    fun getAllByIds(ids: Collection<Long>): List<Product> =
        if (ids.isEmpty()) emptyList() else productRepository.findAllById(ids)

    fun getPopularProducts(
        categoryId: Long?,
        cursorLikes: Int?,
        cursorId: Long?,
        size: Int,
    ): Slice<Product> {
        return productRepository.findPopularProductsByCursor(
            categoryId = categoryId,
            cursorLikes = cursorLikes,
            cursorId = cursorId,
            pageable = PageRequest.of(0, size),
        )
    }

    fun getNewestProducts(
        categoryId: Long?,
        cursorId: Long?,
        size: Int
    ): Slice<Product> {
        return productRepository.findNewestProductsByCursor(
            cutoff = calculateCutoff(),
            categoryId = categoryId,
            cursorId = cursorId,
            pageable = PageRequest.of(0, size),
        )
    }

    private fun calculateCutoff(): Instant =
        instant().minus(NEW_PERIOD_DAYS, ChronoUnit.DAYS)

    companion object {
        private const val NEW_PERIOD_DAYS = 30L
    }
}
