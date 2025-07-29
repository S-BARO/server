package com.dh.baro.cart.domain

import com.dh.baro.core.AbstractTime
import com.dh.baro.identity.domain.Member
import com.dh.baro.product.domain.Product
import jakarta.persistence.*

@Entity
@Table(
    name = "cart_items",
    uniqueConstraints = [UniqueConstraint(columnNames = ["member_id", "product_id"])]
)
class CartItem(
    @Id
    @Column(name = "id")
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product,

    @Column(name = "quantity", nullable = false)
    var quantity: Int = 1
) : AbstractTime()
