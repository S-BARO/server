package com.dh.baro.order.application.event

data class OrderCancelledEvent(
    val orderId: Long,
    val userId: Long,
    val reason: String,
)
