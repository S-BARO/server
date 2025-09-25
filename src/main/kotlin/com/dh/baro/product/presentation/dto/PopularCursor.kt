package com.dh.baro.product.presentation.dto

import com.dh.baro.core.serialization.LongToStringSerializer
import com.fasterxml.jackson.databind.annotation.JsonSerialize

data class PopularCursor(
    @JsonSerialize(using = LongToStringSerializer::class)
    val id: Long,
    val likes: Int,
)
