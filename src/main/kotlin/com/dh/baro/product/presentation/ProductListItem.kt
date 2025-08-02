package com.dh.baro.product.presentation

import com.dh.baro.product.domain.Product
import java.math.BigDecimal

data class ProductListItem(
    val id: Long,
    val name: String,
    val price: BigDecimal,
    val thumbnailUrl: String?,
) {

    companion object {
        fun from(product: Product) = ProductListItem(
            id = product.id,
            name = product.name,
            price = product.price,
            thumbnailUrl = product.thumbnailUrl,
        )
    }
}
