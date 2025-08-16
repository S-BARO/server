package com.dh.baro.product.domain

import com.dh.baro.core.AbstractTime
import com.dh.baro.core.AggregateRoot
import com.dh.baro.core.ErrorMessage
import com.dh.baro.core.IdGenerator
import jakarta.persistence.*
import java.math.BigDecimal

@AggregateRoot
@Entity
@Table(name = "products")
class Product(
    @Id
    @Column(name = "id")
    val id: Long,

    @Column(name = "store_id", nullable = false)
    val storeId: Long,

    @Column(name = "product_name", nullable = false, length = 100)
    private var name: String,

    @Column(name = "price", nullable = false, precision = 10, scale = 0)
    private var price: BigDecimal,

    @Column(name = "quantity", nullable = false)
    private var quantity: Int = 0,

    @Lob
    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    private var description: String? = null,

    @Column(name = "likes_count", nullable = false)
    private var likesCount: Int = 0,

    @Column(name = "thumbnail_url", nullable = false, length = 300)
    private var thumbnailUrl: String,

    @OneToMany(
        mappedBy = "product",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    private val images: MutableSet<ProductImage> = mutableSetOf(),

    @OneToMany(
        mappedBy = "product",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private val productCategories: MutableSet<ProductCategory> = mutableSetOf(),
) : AbstractTime() {

    fun getName(): String = name

    fun getPrice(): BigDecimal = price

    fun getQuantity(): Int = quantity

    fun getDescription(): String? = description

    fun getLikesCount(): Int = likesCount

    fun getThumbnailUrl(): String = thumbnailUrl

    fun getImages(): List<ProductImage> = images.sortedBy { it.displayOrder }

    fun getProductCategories(): List<ProductCategory> = productCategories.toList()

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
            ProductImage.of(
                product = this,
                imageUrl = imageUrl,
                displayOrder = displayOrder,
            )
        )

    fun addCategories(categories: Collection<Category>) {
        categories.forEach { addCategory(it) }
    }

    fun addCategory(category: Category) {
        if (productCategories.any { it.category.id == category.id }) return

        val productCategory = ProductCategory.of(this, category)
        productCategories.add(productCategory)
    }

    fun deductStockForOrder(orderQuantity: Int) {
        require(quantity >= orderQuantity) {
            ErrorMessage.OUT_OF_STOCK.format(id)
        }

        quantity -= orderQuantity
    }

    companion object {
        fun newProduct(
            name: String,
            storeId: Long,
            price: BigDecimal,
            quantity: Int,
            thumbnailUrl: String,
            description: String? = null,
            likesCount: Int = 0,
        ): Product =
            Product(
                id = IdGenerator.generate(),
                storeId = storeId,
                name = name,
                price = price,
                quantity = quantity,
                description = description,
                likesCount = likesCount,
                thumbnailUrl = thumbnailUrl,
            )
    }
}
