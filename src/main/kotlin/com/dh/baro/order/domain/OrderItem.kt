package com.dh.baro.order.domain

import com.dh.baro.core.AbstractTime
import com.dh.baro.product.domain.Product
import jakarta.persistence.*

@Entity
@Table(name = "order_items")
class OrderItem(
    @Id
    @Column(name = "order_item_id")
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    val order: Order,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product,

    @Column(name = "quantity", nullable = false)
    val quantity: Int,

    @Column(name = "price_at_purchase", nullable = false)
    val priceAtPurchase: Int
) : AbstractTime()
