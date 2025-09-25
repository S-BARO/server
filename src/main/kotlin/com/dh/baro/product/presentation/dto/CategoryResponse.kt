package com.dh.baro.product.presentation.dto

import com.dh.baro.core.serialization.LongToStringSerializer
import com.dh.baro.product.domain.Category
import com.fasterxml.jackson.databind.annotation.JsonSerialize

data class CategoryResponse(
    @JsonSerialize(using = LongToStringSerializer::class)
    val id: Long,
    val name: String,
) {

    companion object {
        fun from(entity: Category) = CategoryResponse(
            id = entity.id,
            name = entity.name,
        )
    }
}
