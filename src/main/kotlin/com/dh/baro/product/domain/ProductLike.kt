package com.dh.baro.product.domain

import com.dh.baro.core.AbstractTime
import jakarta.persistence.*

@Entity
@Table(name = "product_likes")
class ProductLike(
    @Id
    val id: Long,

    @Column(name = "user_id")
    val userId: Long,

    @Column(name = "product_id")
    val productId: Long,
) : AbstractTime()
