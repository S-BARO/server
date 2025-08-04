package com.dh.baro.product.presentation.dto

import com.dh.baro.product.domain.Product
import java.math.BigDecimal

data class ProductDetail(
    val id: Long,
    val name: String,
    val price: BigDecimal,
    val description: String?,
    val images: List<String>,
    val categories: List<String>,
) {

    companion object {
        fun from(product: Product) = ProductDetail(
            id = product.id,
            name = product.name,
            price = product.price,
            description = product.description,
            images = product.images.sortedBy { it.displayOrder }.map { it.imageUrl },
            categories = product.productCategories.map { it.category.name },
        )
    }
}
