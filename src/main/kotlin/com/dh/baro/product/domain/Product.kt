package com.dh.baro.product.domain

import com.dh.baro.core.AbstractTime
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "products")
class Product(
    @Id
    @Column(name = "product_id")
    val id: Long,

    @Column(name = "product_name", nullable = false)
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
    val productCategories: MutableList<ProductCategory> = mutableListOf()

) : AbstractTime()
