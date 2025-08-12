package com.dh.baro.product.presentation.dto

import com.dh.baro.product.domain.Product
import java.math.BigDecimal

data class ProductResponse(
    val id: Long,
    val name: String,
    val price: BigDecimal,
    val quantity: Int,
    val thumbnailUrl: String,
    val categoryIds: List<Long>,
) {

    companion object {
        fun from(product: Product) = ProductResponse(
            id = product.id,
            name = product.getName(),
            price = product.getPrice(),
            quantity = product.getQuantity(),
            thumbnailUrl = product.getThumbnailUrl(),
            categoryIds = product.getProductCategories().map { it.category.id },
        )
    }
}
