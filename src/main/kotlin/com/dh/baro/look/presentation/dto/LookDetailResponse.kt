package com.dh.baro.look.presentation.dto

import com.dh.baro.core.serialization.LongToStringSerializer
import com.dh.baro.look.application.dto.LookDetailBundle
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.math.BigDecimal

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LookDetailResponse(
    @JsonSerialize(using = LongToStringSerializer::class)
    val lookId: Long,
    val title: String,
    val description: String?,
    val thumbnailUrl: String,
    val likesCount: Int,
    val lookImageUrls: List<String>,
    val products: List<ProductItemDto>,
) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class ProductItemDto(
        @JsonSerialize(using = LongToStringSerializer::class)
        val productId: Long,
        val storeName: String,
        val productName: String,
        val price: BigDecimal,
        val thumbnailUrl: String,
    )

    companion object {

        val EMPTY = LookDetailResponse(
            lookId = -1L,
            title = "",
            description = null,
            thumbnailUrl = "",
            likesCount = 0,
            lookImageUrls = emptyList(),
            products = emptyList()
        )

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
