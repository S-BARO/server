package com.dh.baro.order.presentation

import com.dh.baro.order.domain.Order
import com.dh.baro.order.domain.OrderStatus
import java.math.BigDecimal
import java.time.Instant

data class OrderListItem(
    val orderId: Long,
    val totalPrice: BigDecimal,
    val orderStatus: OrderStatus,
    val orderedAt: Instant,
) {

    companion object {
        fun from(order: Order) = OrderListItem(
            orderId = order.id,
            totalPrice = order.totalPrice,
            orderStatus = order.status,
            orderedAt = order.createdAt,
        )
    }
}
