package com.dh.baro.order.domain

import com.dh.baro.core.BaseTimeEntity
import com.dh.baro.core.IdGenerator
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "order_items")
class OrderItem(
    @Id
    @Column(name = "id")
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    val order: Order,

    @Column(name = "product_id", nullable = false)
    val productId: Long,

    @Column(name = "quantity", nullable = false)
    val quantity: Int,

    @Column(name = "price_at_purchase", nullable = false, precision = 10, scale = 0)
    val priceAtPurchase: BigDecimal,
) : BaseTimeEntity() {

    override fun getId(): Long = id

    fun subTotal(): BigDecimal =
        priceAtPurchase.multiply(quantity.toBigDecimal())

    companion object {
        fun newOrderItem(
            order: Order,
            productId: Long,
            quantity: Int,
            priceAtPurchase: BigDecimal,
        ): OrderItem =
            OrderItem(
                id = IdGenerator.generate(),
                order = order,
                productId = productId,
                quantity = quantity,
                priceAtPurchase = priceAtPurchase,
            )
    }
}
