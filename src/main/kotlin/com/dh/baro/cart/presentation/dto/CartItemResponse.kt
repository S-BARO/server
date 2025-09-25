package com.dh.baro.cart.presentation.dto

import com.dh.baro.cart.application.CartItemBundle
import com.dh.baro.core.serialization.LongToStringSerializer
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.math.BigDecimal
import java.math.RoundingMode

data class CartItemResponse(
    @JsonSerialize(using = LongToStringSerializer::class)
    val itemId: Long,
    @JsonSerialize(using = LongToStringSerializer::class)
    val productId: Long,
    val productName: String,
    val productThumbnailUrl: String?,
    val price: BigDecimal,
    val quantity: Int,
    val subtotal: BigDecimal,
) {

    companion object {
        private const val SCALE_NONE = 0

        fun from(bundle: CartItemBundle): CartItemResponse {
            val cartItem = bundle.cartItem
            val product = bundle.product

            return CartItemResponse(
                itemId = cartItem.id,
                productId = product.id,
                productName = product.getName(),
                productThumbnailUrl = product.getThumbnailUrl(),
                price = product.getPrice(),
                quantity = cartItem.quantity,
                subtotal = product.getPrice()
                    .multiply(BigDecimal(cartItem.quantity))
                    .setScale(SCALE_NONE, RoundingMode.HALF_UP)
            )
        }
    }
}
