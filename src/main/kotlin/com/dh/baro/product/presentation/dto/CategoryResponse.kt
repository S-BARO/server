package com.dh.baro.product.presentation.dto

import com.dh.baro.product.domain.Category

data class CategoryResponse(
    val id: String,
    val name: String,
) {

    companion object {
        fun from(entity: Category) = CategoryResponse(
            id = entity.id.toString(),
            name = entity.name,
        )
    }
}
