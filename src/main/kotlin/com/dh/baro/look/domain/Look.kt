package com.dh.baro.look.domain

import com.dh.baro.core.annotation.AggregateRoot
import com.dh.baro.core.IdGenerator
import java.time.Instant

@AggregateRoot
data class Look(
    val id: Long,
    val creatorId: Long,
    val title: String,
    val description: String? = null,
    val likesCount: Int = 0,
    val thumbnailUrl: String,
    val images: List<LookImage> = emptyList(),
    val lookProducts: List<LookProduct> = emptyList(),
    val createdAt: Instant? = null,
    val modifiedAt: Instant? = null,
) {

    fun getOrderedProductIds(): List<Long> =
        lookProducts.asSequence()
            .sortedBy { it.displayOrder }
            .map { it.productId }
            .toList()

    fun getOrderedImageUrls(): List<String> =
        images.asSequence()
            .sortedBy { it.displayOrder }
            .map { it.imageUrl }
            .toList()

    fun withImages(imageUrls: List<String>): Look {
        val newImages = imageUrls.mapIndexed { index, url ->
            LookImage.of(
                imageUrl = url,
                displayOrder = images.size + index + 1,
            )
        }
        return copy(images = images + newImages)
    }

    fun withProducts(productIds: List<Long>): Look {
        val existingProductIds = lookProducts.map { it.productId }.toSet()
        val nextOrderStart = lookProducts.size

        val newProducts = productIds
            .distinct()
            .filterNot { it in existingProductIds }
            .mapIndexed { index, productId ->
                LookProduct.of(
                    productId = productId,
                    displayOrder = nextOrderStart + index + 1
                )
            }

        return copy(lookProducts = lookProducts + newProducts)
    }

    companion object {
        fun newLook(
            creatorId: Long,
            title: String,
            description: String?,
            thumbnailUrl: String,
            imageUrls: List<String> = emptyList(),
            productIds: List<Long> = emptyList(),
        ): Look {
            val look = Look(
                id = IdGenerator.generate(),
                creatorId = creatorId,
                title = title,
                description = description,
                thumbnailUrl = thumbnailUrl,
            )
            
            return look
                .withImages(imageUrls)
                .withProducts(productIds)
        }
    }
}
