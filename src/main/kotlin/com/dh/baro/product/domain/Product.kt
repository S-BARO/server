package com.dh.baro.product.domain

import com.dh.baro.core.AbstractTime
import jakarta.persistence.*

@Entity
@Table(name = "products")
class Product(
    @Id
    @Column(name = "product_id")
    val id: Long = 0,

    @Column(name = "product_name", nullable = false)
    var name: String,

    @Column(name = "price", nullable = false)
    var price: Int,

    @Column(name = "quantity", nullable = false)
    var quantity: Int = 0,

    @Lob
    @Column(name = "description")
    var description: String? = null,

    @Column(name = "likes_count", nullable = false)
    var likesCount: Int = 0,

    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], orphanRemoval = true)
    val images: MutableList<ProductImage> = mutableListOf(),

    @ManyToMany
    @JoinTable(
        name = "product_categories",
        joinColumns = [JoinColumn(name = "product_id")],
        inverseJoinColumns = [JoinColumn(name = "category_id")]
    )
    val categories: MutableSet<Category> = mutableSetOf()

) : AbstractTime()
