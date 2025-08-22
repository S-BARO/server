package com.dh.baro.product.domain

import com.dh.baro.core.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(name = "product_likes")
class ProductLike(
    @Id
    @Column(name = "id")
    val id: Long,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "product_id", nullable = false)
    val productId: Long,
) : BaseTimeEntity() {

    override fun getId(): Long = id

}
