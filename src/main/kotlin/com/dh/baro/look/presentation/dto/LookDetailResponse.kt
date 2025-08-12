package com.dh.baro.look.presentation.dto

import com.dh.baro.look.domain.Look
import com.dh.baro.look.domain.vo.LookImageView
import com.dh.baro.look.domain.vo.LookProductView
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
            images: List<LookImageView>,
            lookProducts: List<LookProductView>,
            products: List<Product>,
        ): LookDetailResponse {
            val productMap = products.associateBy { it.id }

            val productDtos = lookProducts.mapNotNull { lp ->
                val p = productMap[lp.productId] ?: return@mapNotNull null
                ProductItemDto(
                    productId = p.id,
                    name = p.getName(),
                    price = p.getPrice(),
                    thumbnailUrl = p.getThumbnailUrl(),
                    displayOrder = lp.displayOrder,
                )
            }

            return LookDetailResponse(
                lookId = look.id,
                title = look.getTitle(),
                description = look.getDescription(),
                thumbnailUrl = look.getThumbnailUrl(),
                likesCount = look.getLikesCount(),
                images = images.map { ImageDto(it.imageUrl, it.displayOrder) },
                products = productDtos,
            )
        }
    }
}
