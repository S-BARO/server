package com.dh.baro.product.application.event

import com.dh.baro.product.domain.InventoryItem

data class RedisStockDeductionEvent(
    val orderId: Long,
    val items: List<InventoryItem>,
)
