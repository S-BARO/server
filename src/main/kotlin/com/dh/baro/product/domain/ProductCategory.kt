package com.dh.baro.product.domain

import com.dh.baro.core.IdGenerator
import jakarta.persistence.*

@Entity
@Table(name = "product_categories")
class ProductCategory(
    @Id
    @Column(name = "id")
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    val category: Category,
) {

    companion object {
        fun of(product: Product, category: Category): ProductCategory =
            ProductCategory(
                id = IdGenerator.generate(),
                product = product,
                category = category,
            )
    }
}
