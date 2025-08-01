package com.dh.baro.cart.presentation.dto

import jakarta.validation.constraints.Min

data class UpdateQuantityRequest(
    @field:Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
    val quantity: Int,
)
