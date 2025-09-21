package com.dh.baro.order.application.event

import com.dh.baro.core.IdGenerator
import com.dh.baro.product.domain.InventoryItem

data class OrderPlacedEvent(
    val eventId: Long = IdGenerator.generate(),
    val orderId: Long,
    val userId: Long,
    val items: List<InventoryItem>,
)
