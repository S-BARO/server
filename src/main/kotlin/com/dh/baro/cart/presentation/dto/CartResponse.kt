package com.dh.baro.cart.presentation.dto

import com.dh.baro.cart.domain.CartItem
import java.math.BigDecimal

data class CartResponse(
    val items: List<CartItemResponse>,
    val totalPrice: BigDecimal
) {
    companion object {
        fun from(cartItems: List<CartItem>): CartResponse {
            val responses = cartItems.map { it.toResponse() }
            val total = responses.fold(BigDecimal.ZERO) { sum, item -> sum + item.subtotal }
            return CartResponse(responses, total)
        }

        private fun CartItem.toResponse() = CartItemResponse(
            itemId = id,
            productId = product.id,
            productName = product.name,
            productImageUrl = product.getThumbnailUrl(),
            price = product.price,
            quantity = quantity,
            subtotal = product.price * BigDecimal(quantity)
        )
    }
}
