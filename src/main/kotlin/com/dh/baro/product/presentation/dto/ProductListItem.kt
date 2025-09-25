package com.dh.baro.product.presentation.dto

import com.dh.baro.core.serialization.LongToStringSerializer
import com.dh.baro.identity.domain.Store
import com.dh.baro.product.domain.Product
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.math.BigDecimal

data class ProductListItem(
    @JsonSerialize(using = LongToStringSerializer::class)
    val id: Long,
    val storeName: String,
    val productName: String,
    val price: BigDecimal,
    val thumbnailUrl: String,
) {
    companion object {
        fun ofOrNull(product: Product, storeMap: Map<Long, Store>): ProductListItem? {
            val store = storeMap[product.storeId]?: return null
            return ProductListItem(
                id = product.id,
                storeName = store.getName(),
                productName = product.getName(),
                price = product.getPrice(),
                thumbnailUrl = product.getThumbnailUrl(),
            )
        }
    }
}
