package com.dh.baro.order.presentation.dto

import com.dh.baro.core.LongToStringSerializer
import com.dh.baro.order.domain.Order
import com.dh.baro.order.domain.OrderStatus
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.math.BigDecimal
import java.time.Instant

data class OrderSummary(
    @JsonSerialize(using = LongToStringSerializer::class)
    val orderId: Long,
    val totalPrice: BigDecimal,
    val orderStatus: OrderStatus,
    val orderedAt: Instant?,
) {

    companion object {
        fun from(order: Order) = OrderSummary(
            orderId = order.id,
            totalPrice = order.totalPrice,
            orderStatus = order.status,
            orderedAt = order.createdAt,
        )
    }
}
