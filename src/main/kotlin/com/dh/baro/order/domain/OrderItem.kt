package com.dh.baro.order.domain

import com.dh.baro.core.AbstractTime
import com.dh.baro.core.IdGenerator
import com.dh.baro.product.domain.Product
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product,

    @Column(name = "quantity", nullable = false)
    val quantity: Int,

    @Column(name = "price_at_purchase", nullable = false)
    val priceAtPurchase: BigDecimal,
) : AbstractTime() {

    fun subTotal(): BigDecimal =
        priceAtPurchase.multiply(quantity.toBigDecimal())

    companion object {
        fun newOrderItem(
            order: Order,
            product: Product,
            quantity: Int
        ): OrderItem =
            OrderItem(
                id = IdGenerator.generate(),
                order = order,
                product = product,
                quantity = quantity,
                priceAtPurchase = product.getPrice(),
            )
    }
}
