package com.dh.baro.product.domain

import com.dh.baro.core.AbstractTime
import com.dh.baro.core.IdGenerator
import jakarta.persistence.*

@Entity
@Table(name = "product_images")
class ProductImage(
    @Id
    @Column(name = "id")
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product,

    @Column(name = "image_url", nullable = false, length = 300)
    val imageUrl: String,

    @Column(name = "display_order", nullable = false)
    val displayOrder: Int,
) : AbstractTime() {

    companion object {
        fun of(
            product: Product,
            imageUrl: String,
            displayOrder: Int
        ) = ProductImage(
            id = IdGenerator.generate(),
            product = product,
            imageUrl = imageUrl,
            displayOrder = displayOrder,
        )
    }
}

