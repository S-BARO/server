package com.dh.baro.product.domain.event

data class InventoryDeductionRequestedEvent(
    val orderId: Long,
    val userId: Long,
    val items: List<InventoryItem>
) {
    data class InventoryItem(
        val productId: Long,
        val quantity: Int
    )
}
