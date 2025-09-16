package com.dh.baro.product.application.event

data class InventoryInsufficientEvent(
    val orderId: Long,
    val productId: Long,
    val requestedQuantity: Int,
)
