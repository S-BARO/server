package com.dh.baro.product.domain

import com.dh.baro.core.AbstractTime
import com.dh.baro.core.IdGenerator
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "products")
class Product(
    @Id
    @Column(name = "id")
    val id: Long,

    @Column(name = "product_name", nullable = false, length = 100)
    var name: String,

    @Column(name = "price", nullable = false, precision = 10, scale = 0)
    var price: BigDecimal,

    @Column(name = "quantity", nullable = false)
    var quantity: Int = 0,

    @Lob
    @Column(name = "description")
    var description: String? = null,

    @Column(name = "likes_count", nullable = false)
    var likesCount: Int = 0,

    @Column(name = "thumbnail_url", nullable = false, length = 300)
    var thumbnailUrl: String,

    @OneToMany(
        mappedBy = "product",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    val images: MutableList<ProductImage> = mutableListOf(),

    @OneToMany(
        mappedBy = "product",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    val productCategories: MutableList<ProductCategory> = mutableListOf(),

) : AbstractTime() {

    fun addCategory(category: Category) {
        if (productCategories.any { it.category == category }) return

        val pc = ProductCategory.of(this, category)
        productCategories.add(pc)
    }

    companion object {
        fun newProduct(
            name: String,
            price: BigDecimal,
            quantity: Int,
            thumbnailUrl: String,
            description: String? = null,
            likesCount: Int = 0,
        ): Product =
            Product(
                id = IdGenerator.generate(),
                name = name,
                price = price,
                quantity = quantity,
                description = description,
                likesCount = likesCount,
                thumbnailUrl = thumbnailUrl,
            )
    }
}
