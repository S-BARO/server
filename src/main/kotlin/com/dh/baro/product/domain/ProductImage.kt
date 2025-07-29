package com.dh.baro.product.domain

import com.dh.baro.core.AbstractTime
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

    @Column(name = "is_thumbnail", nullable = false)
    val isThumbnail: Boolean = false
) : AbstractTime()
