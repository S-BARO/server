package com.dh.baro.product.presentation.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CategoryCreateRequest(
    @field:NotBlank
    val id: Long,

    @field:Size(min = 1, max = 50)
    val name: String,
)
