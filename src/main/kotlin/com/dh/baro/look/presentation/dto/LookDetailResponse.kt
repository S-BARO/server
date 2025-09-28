package com.dh.baro.look.presentation.dto

import com.dh.baro.core.serialization.LongToStringSerializer
import com.dh.baro.look.application.dto.LookDetailBundle
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.math.BigDecimal

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LookDetailResponse @JsonCreator constructor(
    @JsonProperty("lookId") @JsonSerialize(using = LongToStringSerializer::class)
    val lookId: Long,
    @JsonProperty("title")
    val title: String,
    @JsonProperty("description")
    val description: String?,
    @JsonProperty("thumbnailUrl")
    val thumbnailUrl: String,
    @JsonProperty("likesCount")
    val likesCount: Int,
    @JsonProperty("lookImageUrls")
    val lookImageUrls: List<String>,
    @JsonProperty("products")
    val products: List<ProductItemDto>,
) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class ProductItemDto @JsonCreator constructor(
        @JsonProperty("productId") @JsonSerialize(using = LongToStringSerializer::class)
        val productId: Long,
        @JsonProperty("storeName")
        val storeName: String,
        @JsonProperty("productName")
        val productName: String,
        @JsonProperty("price")
        val price: BigDecimal,
        @JsonProperty("thumbnailUrl")
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
