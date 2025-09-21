package com.dh.baro.product.application.event

import com.dh.baro.core.IdGenerator

data class InventoryInsufficientEvent(
    val orderId: Long,
    val productId: Long,
    val requestedQuantity: Int,
    val eventId: Long = IdGenerator.generate()
)
