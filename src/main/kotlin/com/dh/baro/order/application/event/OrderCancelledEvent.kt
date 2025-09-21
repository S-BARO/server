package com.dh.baro.order.application.event

import com.dh.baro.core.IdGenerator

data class OrderCancelledEvent(
    val orderId: Long,
    val userId: Long,
    val reason: String,
    val eventId: Long = IdGenerator.generate()
)
