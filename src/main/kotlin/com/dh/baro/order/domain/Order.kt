package com.dh.baro.order.domain

import com.dh.baro.core.AbstractTime
import com.dh.baro.core.anotation.AggregateRoot
import com.dh.baro.core.IdGenerator
import jakarta.persistence.*
import java.math.BigDecimal
import java.math.RoundingMode

@AggregateRoot
@Entity
@Table(name = "orders")
class Order(
    @Id
    @Column(name = "id")
    val id: Long,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "total_price", nullable = false, precision = 10, scale = 0)
    var totalPrice: BigDecimal,

    @Column(name = "shipping_address", nullable = false, length = 500)
    var shippingAddress: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false, length = 20)
    var status: OrderStatus = OrderStatus.ORDERED,

    @OneToMany(
        mappedBy = "order",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    val items: MutableSet<OrderItem> = mutableSetOf()
) : AbstractTime() {

    fun addItem(item: OrderItem) =
        items.add(item)

    fun updateTotalPrice() {
        totalPrice = items
            .map { it.subTotal() }
            .reduceOrNull(BigDecimal::add) ?: BigDecimal.ZERO
            .setScale(0, RoundingMode.HALF_UP)
    }

    companion object {
        fun newOrder(
            userId: Long,
            shippingAddress: String
        ): Order =
            Order(
                id = IdGenerator.generate(),
                userId = userId,
                totalPrice = BigDecimal.ZERO,
                shippingAddress = shippingAddress,
                status = OrderStatus.ORDERED,
            )
    }
}
