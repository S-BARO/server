package com.dh.baro.product.infra.event

import com.dh.baro.product.domain.InventoryItem

data class InventoryDeductionRequestedEvent(
    val orderId: Long,
    val userId: Long,
    val items: List<InventoryItem>,
)
