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
            name = product.getName(),
            price = product.getPrice(),
            description = product.getDescription(),
            images = product.getImages().map { it.imageUrl },
            categories = product.getProductCategories().map { it.category.name },
        )
    }
}
