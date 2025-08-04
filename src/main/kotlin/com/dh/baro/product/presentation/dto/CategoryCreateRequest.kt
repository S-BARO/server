package com.dh.baro.product.presentation.dto

import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class CategoryCreateRequest(
    @field:Positive
    val id: Long,

    @field:Size(min = 1, max = 50)
    val name: String,
)
