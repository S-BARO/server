package com.dh.baro.order.presentation

data class OrderCreateRequest(
    val shippingAddress: String,
    val orderItems: List<OrderItem>,
) {

    data class OrderItem(
        val productId: Long,
        val quantity: Int,
    )
}
