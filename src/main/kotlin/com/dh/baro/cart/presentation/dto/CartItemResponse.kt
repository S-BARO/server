package com.dh.baro.cart.presentation.dto

import com.dh.baro.core.LongToStringSerializer
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.math.BigDecimal

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
)
