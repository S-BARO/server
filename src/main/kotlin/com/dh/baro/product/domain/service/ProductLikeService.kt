package com.dh.baro.product.domain.service

import com.dh.baro.product.domain.ProductLike
import com.dh.baro.product.domain.repository.ProductLikeRepository
import com.dh.baro.product.domain.repository.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductLikeService(
    private val productLikeRepository: ProductLikeRepository,
    private val productRepository: ProductRepository,
) {

    @Transactional
    fun likeProduct(userId: Long, productId: Long) {
        if (productLikeRepository.existsByUserIdAndProductId(userId, productId)) {
            return
        }

        val productLike = ProductLike.create(userId, productId)
        productLikeRepository.save(productLike)
        productRepository.incrementLikesCount(productId)
    }

    @Transactional
    fun cancelProductLike(userId: Long, productId: Long) {
        val deletedCount = productLikeRepository.deleteByUserIdAndProductId(userId, productId)
        if (deletedCount > 0) {
            productRepository.decrementLikesCount(productId)
        }
    }
}
