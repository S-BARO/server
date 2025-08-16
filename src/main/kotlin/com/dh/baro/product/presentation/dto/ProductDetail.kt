package com.dh.baro.product.presentation.dto

import com.dh.baro.identity.domain.Store
import com.dh.baro.product.domain.Product
import java.math.BigDecimal

data class ProductDetail(
    val id: Long,
    val storeName: String,
    val productName: String,
    val price: BigDecimal,
    val description: String?,
    val images: List<String>,
    val categories: List<String>,
) {

    companion object {
        fun from(product: Product, store: Store) = ProductDetail(
            id = product.id,
            storeName = store.getName(),
            productName = product.getName(),
            price = product.getPrice(),
            description = product.getDescription(),
            images = product.getImages().map { it.imageUrl },
            categories = product.getProductCategories().map { it.category.name },
        )
    }
}
