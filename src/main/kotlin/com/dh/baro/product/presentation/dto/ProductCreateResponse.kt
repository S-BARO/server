package com.dh.baro.product.presentation.dto

import com.dh.baro.core.serialization.LongToStringSerializer
import com.dh.baro.product.domain.Product
import com.fasterxml.jackson.databind.annotation.JsonSerialize

data class ProductCreateResponse(
    @JsonSerialize(using = LongToStringSerializer::class)
    val id: Long,
    val name: String,
) {

    companion object {
        fun from(product: Product) = ProductCreateResponse(
            id = product.id,
            name = product.getName(),
        )
    }
}
