package com.dh.baro.look.presentation.dto

import com.dh.baro.look.domain.Look
import com.dh.baro.look.domain.LookImage
import com.dh.baro.look.domain.LookProduct
import com.dh.baro.product.domain.Product
import java.math.BigDecimal

data class LookDetailResponse(
    val lookId: Long,
    val title: String,
    val description: String?,
    val thumbnailUrl: String,
    val likesCount: Int,
    val images: List<ImageDto>,
    val products: List<ProductItemDto>,
) {
    data class ImageDto(
        val imageUrl: String,
        val displayOrder: Int,
    )

    data class ProductItemDto(
        val productId: Long,
        val name: String,
        val price: BigDecimal,
        val thumbnailUrl: String,
        val displayOrder: Int,
    )

    companion object {
        fun of(
            look: Look,
            images: List<LookImage>,
            lookProducts: List<LookProduct>,
            products: List<Product>,
        ): LookDetailResponse {
            val imageDtos = images
                .sortedBy { it.displayOrder }
                .map { ImageDto(it.imageUrl, it.displayOrder) }

            val productMap = products.associateBy { it.id }
            val productDtos = lookProducts
                .sortedBy { it.displayOrder }
                .mapNotNull { lp ->
                    val p = productMap[lp.productId] ?: return@mapNotNull null
                    ProductItemDto(
                        productId = p.id,
                        name = p.name,
                        price = p.price,
                        thumbnailUrl = p.thumbnailUrl,
                        displayOrder = lp.displayOrder,
                    )
                }

            return LookDetailResponse(
                lookId = look.id,
                title = look.title,
                description = look.description,
                thumbnailUrl = look.thumbnailUrl,
                likesCount = look.likesCount,
                images = imageDtos,
                products = productDtos,
            )
        }
    }
}
