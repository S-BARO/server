package com.dh.baro.cart.presentation.dto

import com.dh.baro.cart.application.CartItemBundle
import java.math.BigDecimal
import java.math.RoundingMode

data class CartResponse(
    val items: List<CartItemResponse>,
    val totalPrice: BigDecimal
) {
    companion object {
        private const val SCALE_NONE = 0

        fun from(bundles: List<CartItemBundle>): CartResponse {
            val items = bundles.map { bundle ->
                CartItemResponse.from(bundle)
            }

            val totalPrice = items.fold(BigDecimal.ZERO) { sum, item ->
                sum + item.subtotal
            }.setScale(SCALE_NONE, RoundingMode.HALF_UP)

            return CartResponse(items, totalPrice)
        }
    }
}
