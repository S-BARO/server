package com.dh.baro.look.domain

import com.dh.baro.core.AbstractTime
import com.dh.baro.core.IdGenerator
import jakarta.persistence.*

@Entity
@Table(name = "looks")
class Look(
    @Id
    @Column(name = "id")
    val id: Long,

    @Column(name = "creator_id", nullable = false)
    val creatorId: Long,

    @Column(name = "title", nullable = false)
    var title: String,

    @Lob @Column(name = "description")
    var description: String? = null,

    @Column(name = "likes_count", nullable = false)
    var likesCount: Int = 0,

    @Column(name = "thumbnail_url", nullable = false, length = 300)
    var thumbnailUrl: String,

    @OneToMany(
        mappedBy = "look",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    val images: MutableSet<LookImage> = mutableSetOf(),

    @OneToMany(
        mappedBy = "look",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    private val lookProducts: MutableSet<LookProduct> = mutableSetOf(),
) : AbstractTime() {

    fun addImages(imageUrls: List<String>) {
        imageUrls.forEachIndexed { idx, url ->
            addImage(
                imageUrl = url,
                displayOrder = images.size + idx + 1,
            )
        }
    }

    private fun addImage(imageUrl: String, displayOrder: Int) =
        images.add(
            LookImage.of(
                look = this,
                imageUrl = imageUrl,
                displayOrder = displayOrder,
            )
        )

    fun addProducts(productIds: List<Long>) {
        val nextOrderStart = lookProducts.size
        productIds
            .distinct()
            .filterNot { id ->
                lookProducts.any { it.productId == id }
            }
            .forEachIndexed { idx, id ->
                addProduct(id, nextOrderStart + idx + 1)
            }
    }

    private fun addProduct(productId: Long, displayOrder: Int) {
        lookProducts += LookProduct.of(
            look = this,
            productId = productId,
            displayOrder = displayOrder,
        )
    }

    companion object {
        fun newLook(
            creatorId: Long,
            title: String,
            description: String?,
            thumbnailUrl: String,
        ): Look =
            Look(
                id = IdGenerator.generate(),
                creatorId = creatorId,
                title = title,
                description = description,
                thumbnailUrl = thumbnailUrl,
            )
    }
}
