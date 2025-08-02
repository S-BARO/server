package com.dh.baro.cart.presentation.dto

import java.math.BigDecimal

data class CartItemResponse(
    val itemId: Long,
    val productId: Long,
    val productName: String,
    val productThumbnailUrl: String?,
    val price: BigDecimal,
    val quantity: Int,
    val subtotal: BigDecimal,
)
