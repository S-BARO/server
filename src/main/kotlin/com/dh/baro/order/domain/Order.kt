package com.dh.baro.order.domain

import com.dh.baro.core.BaseTimeEntity
import com.dh.baro.core.ErrorMessage
import com.dh.baro.core.IdGenerator
import com.dh.baro.core.annotation.AggregateRoot
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
    var status: OrderStatus = OrderStatus.PENDING,

    @OneToMany(
        mappedBy = "order",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    val items: MutableSet<OrderItem> = mutableSetOf(),
) : BaseTimeEntity() {

    override fun getId(): Long = id

    fun addItem(item: OrderItem) =
        items.add(item)

    fun updateTotalPrice() {
        totalPrice = items
            .map { it.subTotal() }
            .reduceOrNull(BigDecimal::add) ?: BigDecimal.ZERO
            .setScale(0, RoundingMode.HALF_UP)
    }

    fun confirmOrder() {
        if (status != OrderStatus.PENDING) {
            throw IllegalStateException(ErrorMessage.ORDER_CONFIRM_INVALID_STATUS.format(status))
        }

        if (items.isEmpty()) {
            throw IllegalStateException(ErrorMessage.ORDER_CONFIRM_NO_ITEMS.message)
        }

        status = OrderStatus.ORDERED
    }

    fun cancel(reason: String) {
        if (status == OrderStatus.CANCELLED) {
            throw IllegalStateException(ErrorMessage.ORDER_ALREADY_CANCELLED.format(id))
        }

        if (status in listOf(OrderStatus.SHIPPED, OrderStatus.DELIVERED)) {
            throw IllegalStateException(ErrorMessage.ORDER_CANCEL_INVALID_STATUS.format(status, id))
        }

        status = OrderStatus.CANCELLED
    }

    fun changeStatus(newStatus: OrderStatus) {
        this.status = newStatus
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
                status = OrderStatus.PENDING,
            )
    }
}
