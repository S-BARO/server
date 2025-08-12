package com.dh.baro.product.presentation.dto

import com.dh.baro.product.domain.Product

data class ProductCreateResponse(
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
