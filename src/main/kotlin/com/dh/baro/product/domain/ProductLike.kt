package com.dh.baro.product.domain

import com.dh.baro.core.BaseTimeEntity
import com.dh.baro.core.IdGenerator
import jakarta.persistence.*

@Entity
@Table(
    name = "product_likes",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_product_likes_user_product",
            columnNames = ["user_id", "product_id"]
        )
    ],
    indexes = [
        Index(name = "idx_product_likes_product_id", columnList = "product_id")
    ]
)
class ProductLike private constructor(
    @Id
    @Column(name = "id")
    val id: Long,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "product_id", nullable = false)
    val productId: Long,
) : BaseTimeEntity() {

    override fun getId(): Long = id

    companion object {
        fun create(userId: Long, productId: Long): ProductLike {
            return ProductLike(
                id = IdGenerator.generate(),
                userId = userId,
                productId = productId,
            )
        }
    }
}
