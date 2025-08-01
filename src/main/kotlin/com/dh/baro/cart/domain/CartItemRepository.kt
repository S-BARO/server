package com.dh.baro.cart.domain

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface CartItemRepository : JpaRepository<CartItem, Long> {

    @EntityGraph(attributePaths = ["product"])
    fun findByUserId(userId: Long): List<CartItem>

    fun findByUserIdAndProductId(userId: Long, productId: Long): CartItem?

    fun findByIdAndUserId(id: Long, userId: Long): CartItem?

    fun countByUserId(userId: Long): Long
}
