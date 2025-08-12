package com.dh.baro.look.domain

import com.dh.baro.core.IdGenerator
import jakarta.persistence.*

@Entity
@Table(
    name = "look_products",
    uniqueConstraints = [UniqueConstraint(columnNames = ["look_id", "product_id"])],
)
class LookProduct(
    @Id
    @Column(name = "id")
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "look_id", nullable = false, updatable = false)
    val look: Look,

    @Column(name = "product_id", nullable = false, updatable = false)
    val productId: Long,

    @Column(name = "display_order", nullable = false)
    val displayOrder: Int,
) {

    companion object {
        fun of(
            look: Look,
            productId: Long,
            displayOrder: Int
        ) = LookProduct(
                id = IdGenerator.generate(),
                look = look,
                productId = productId,
                displayOrder = displayOrder,
            )
    }
}
