package com.dh.baro.order.presentation.dto

import com.dh.baro.order.domain.Order
import com.dh.baro.order.domain.OrderStatus
import java.math.BigDecimal
import java.time.Instant

data class OrderSummary(
    val orderId: String,
    val totalPrice: BigDecimal,
    val orderStatus: OrderStatus,
    val orderedAt: Instant?,
) {

    companion object {
        fun from(order: Order) = OrderSummary(
            orderId = order.id.toString(),
            totalPrice = order.totalPrice,
            orderStatus = order.status,
            orderedAt = order.createdAt,
        )
    }
}
