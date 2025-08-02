package com.dh.baro.cart.presentation.dto

import com.dh.baro.cart.domain.CartItem
import java.math.BigDecimal
import java.math.RoundingMode

data class CartResponse(
    val items: List<CartItemResponse>,
    val totalPrice: BigDecimal
) {
    companion object {
        private const val SCALE_NONE = 0

        fun from(cartItems: List<CartItem>): CartResponse {
            val responses = cartItems.map { it.toResponse() }
            val total = responses.fold(BigDecimal.ZERO) { sum, item -> sum + item.subtotal }
                .setScale(SCALE_NONE, RoundingMode.HALF_UP)
            return CartResponse(responses, total)
        }

        private fun CartItem.toResponse() = CartItemResponse(
            itemId = id,
            productId = product.id,
            productName = product.name,
            productThumbnailUrl = product.thumbnailUrl,
            price = product.price,
            quantity = quantity,
            subtotal = product.price
                .multiply(BigDecimal(quantity))
                .setScale(SCALE_NONE, RoundingMode.HALF_UP)
        )
    }
}
