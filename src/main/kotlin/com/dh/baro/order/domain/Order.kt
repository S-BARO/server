package com.dh.baro.order.domain

import com.dh.baro.core.AbstractTime
import com.dh.baro.core.IdGenerator
import com.dh.baro.identity.domain.User
import jakarta.persistence.*
import java.math.BigDecimal
import java.math.RoundingMode

@Entity
@Table(name = "orders")
class Order(
    @Id
    @Column(name = "id")
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(name = "total_price", nullable = false)
    var totalPrice: BigDecimal,

    @Column(name = "shipping_address", nullable = false, length = 500)
    var shippingAddress: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: OrderStatus = OrderStatus.ORDERED,

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true)
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
            user: User,
            shippingAddress: String
        ): Order =
            Order(
                id = IdGenerator.generate(),
                user = user,
                totalPrice = BigDecimal.ZERO,
                shippingAddress = shippingAddress,
                status = OrderStatus.ORDERED,
            )
    }
}
