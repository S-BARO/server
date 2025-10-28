package com.dh.baro.product.domain.repository

import com.dh.baro.product.domain.ProductLike
import org.springframework.data.jpa.repository.JpaRepository

interface ProductLikeRepository : JpaRepository<ProductLike, Long> {

    fun existsByUserIdAndProductId(userId: Long, productId: Long): Boolean

    fun deleteByUserIdAndProductId(userId: Long, productId: Long): Int

    fun findAllByUserIdAndProductIdIn(userId: Long, productIds: Collection<Long>): List<ProductLike>
}
