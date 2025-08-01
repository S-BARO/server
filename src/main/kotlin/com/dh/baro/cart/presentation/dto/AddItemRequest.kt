package com.dh.baro.cart.presentation.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Positive

data class AddItemRequest(
    @field:Positive(message = "유효한 상품 ID를 입력해주세요.")
    val productId: Long,
    @field:Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
    val quantity: Int,
)
