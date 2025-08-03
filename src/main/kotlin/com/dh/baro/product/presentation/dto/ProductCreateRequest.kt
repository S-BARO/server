package com.dh.baro.product.presentation.dto

import jakarta.validation.constraints.*
import java.math.BigDecimal

data class ProductCreateRequest(
    @field:Size(min = 1, max = 100)
    val name: String,

    @field:Positive
    val price: BigDecimal,

    @field:PositiveOrZero
    val quantity: Int,

    val description: String? = null,

    @field:PositiveOrZero
    val likesCount: Int,

    @field:NotBlank
    val thumbnailUrl: String,

    @field:NotEmpty
    val categoryIds: List<Long>,
)
