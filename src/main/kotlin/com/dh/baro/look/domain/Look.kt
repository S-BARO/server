package com.dh.baro.look.domain

import com.dh.baro.core.AbstractTime
import com.dh.baro.core.IdGenerator
import com.dh.baro.look.domain.vo.LookImageView
import com.dh.baro.look.domain.vo.LookProductView
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
    private var title: String,

    @Lob @Column(name = "description")
    private var description: String? = null,

    @Column(name = "likes_count", nullable = false)
    private var likesCount: Int = 0,

    @Column(name = "thumbnail_url", nullable = false, length = 300)
    private var thumbnailUrl: String,

    @OneToMany(
        mappedBy = "look",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    private val images: MutableSet<LookImage> = mutableSetOf(),

    @OneToMany(
        mappedBy = "look",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    private val products: MutableSet<LookProduct> = mutableSetOf(),
) : AbstractTime() {

    fun getTitle() = title

    fun getDescription() = description

    fun getLikesCount() = likesCount

    fun getThumbnailUrl() = thumbnailUrl

    fun getOrderedImageViews(): List<LookImageView> =
        images.asSequence()
            .sortedBy { it.displayOrder }
            .map { LookImageView(it.imageUrl, it.displayOrder) }
            .toList()

    fun getOrderedProductViews(): List<LookProductView> =
        products.asSequence()
            .sortedBy { it.displayOrder }
            .map { LookProductView(it.productId, it.displayOrder) }
            .toList()

    fun addImages(imageUrls: List<String>) {
        val start = images.size
        imageUrls.forEachIndexed { idx, url ->
            addImage(
                imageUrl = url,
                displayOrder = start + idx + 1,
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
        val nextOrderStart = products.size
        productIds
            .distinct()
            .filterNot { id ->
                products.any { it.productId == id }
            }
            .forEachIndexed { idx, id ->
                addProduct(id, nextOrderStart + idx + 1)
            }
    }

    private fun addProduct(productId: Long, displayOrder: Int) {
        products += LookProduct.of(
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
