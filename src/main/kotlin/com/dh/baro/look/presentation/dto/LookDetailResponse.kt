package com.dh.baro.look.presentation.dto

import com.dh.baro.look.application.dto.LookDetailBundle
import java.math.BigDecimal

data class LookDetailResponse(
    val lookId: Long,
    val title: String,
    val description: String?,
    val thumbnailUrl: String,
    val likesCount: Int,
    val lookImageUrls: List<String>,
    val products: List<ProductItemDto>,
) {

    data class ProductItemDto(
        val productId: Long,
        val storeName: String,
        val productName: String,
        val price: BigDecimal,
        val thumbnailUrl: String,
    )

    companion object {

        fun from(bundle: LookDetailBundle): LookDetailResponse {
            val look = bundle.look

            val productMap = bundle.products.associateBy { it.id }
            val storeMap = bundle.stores.associateBy { it.id }

            val productItemDtos = bundle.orderedProductIds.mapNotNull { productId ->
                val product = productMap[productId] ?: return@mapNotNull null
                val store = storeMap[product.storeId]?: return@mapNotNull null

                ProductItemDto(
                    productId = product.id,
                    storeName = store.getName(),
                    productName = product.getName(),
                    price = product.getPrice(),
                    thumbnailUrl = product.getThumbnailUrl(),
                )
            }

            return LookDetailResponse(
                lookId = look.id,
                title = look.getTitle(),
                description = look.getDescription(),
                thumbnailUrl = look.getThumbnailUrl(),
                likesCount = look.getLikesCount(),
                lookImageUrls = look.getOrderedImageUrls(),
                products = productItemDtos,
            )
        }
    }
}
