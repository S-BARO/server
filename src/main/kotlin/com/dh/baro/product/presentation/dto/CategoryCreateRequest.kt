package com.dh.baro.product.presentation.dto

import com.dh.baro.core.StringToLongDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CategoryCreateRequest(
    @JsonDeserialize(using = StringToLongDeserializer::class)
    @field:NotNull
    val id: Long,

    @field:Size(min = 1, max = 50)
    val name: String,
)
