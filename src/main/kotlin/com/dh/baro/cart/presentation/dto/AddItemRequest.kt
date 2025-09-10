package com.dh.baro.cart.presentation.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class AddItemRequest(
    @field:NotNull(message = "상품 ID를 입력해주세요.")
    val productId: Long,
    @field:Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
    val quantity: Int,
)
