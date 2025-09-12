package com.dh.baro.look.infra.mysql

import com.dh.baro.core.BaseTimeEntity
import com.dh.baro.look.domain.Look
import com.dh.baro.look.domain.LookProduct
import jakarta.persistence.*

@Entity
@Table(
    name = "look_products",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["look_id", "product_id"]),
        UniqueConstraint(columnNames = ["look_id", "display_order"])
    ],
)
class LookProductEntity(
    @Id
    @Column(name = "id")
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "look_id", nullable = false, updatable = false)
    val look: LookEntity,

    @Column(name = "product_id", nullable = false, updatable = false)
    val productId: Long,

    @Column(name = "display_order", nullable = false)
    val displayOrder: Int,
): BaseTimeEntity() {

    override fun getId(): Long = id

    fun toDomain(): LookProduct =
        LookProduct(
            id = id,
            productId = productId,
            displayOrder = displayOrder,
        )

    companion object {
        fun fromDomain(
            look: Look, lookProduct: LookProduct
        ) = LookProductEntity(
                id = lookProduct.id,
                look = LookEntity.fromDomain(look),
                productId = lookProduct.productId,
                displayOrder = lookProduct.displayOrder,
            )
    }
}
