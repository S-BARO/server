package com.dh.baro.product.application.event

import com.dh.baro.core.IdGenerator
import com.dh.baro.product.domain.InventoryItem

data class InventoryDeductionCompletedEvent(
    val orderId: Long,
    val userId: Long,
    val items: List<InventoryItem>,
    val eventId: Long = IdGenerator.generate()
)
