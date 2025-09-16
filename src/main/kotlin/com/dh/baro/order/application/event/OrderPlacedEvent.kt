package com.dh.baro.order.application.event

import com.dh.baro.product.domain.InventoryItem

data class OrderPlacedEvent(
    val orderId: Long,
    val userId: Long,
    val items: List<InventoryItem>,
)
