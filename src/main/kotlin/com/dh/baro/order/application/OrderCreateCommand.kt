package com.dh.baro.order.application

import com.dh.baro.order.presentation.OrderCreateRequest

data class OrderCreateCommand(
    val userId: Long,
    val shippingAddress: String,
    val items: List<Item>,
) {

    data class Item(
        val productId: Long,
        val quantity: Int,
    ) {

        companion object {
            fun of(orderItem: OrderCreateRequest.OrderItem): Item =
                Item(orderItem.productId, orderItem.quantity)
        }
    }

    companion object {
        fun toCommand(userId: Long, request: OrderCreateRequest): OrderCreateCommand {
            return OrderCreateCommand(
                userId = userId,
                shippingAddress = request.shippingAddress,
                items = request.orderItems.map { Item.of(it) },
            )
        }
    }
}
