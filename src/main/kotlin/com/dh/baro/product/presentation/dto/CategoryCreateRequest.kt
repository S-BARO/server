package com.dh.baro.product.presentation.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CategoryCreateRequest(
    @field:NotNull
    val id: Long,

    @field:Size(min = 1, max = 50)
    val name: String,
)
