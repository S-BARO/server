package com.dh.baro.order.application

import com.dh.baro.identity.domain.User
import com.dh.baro.order.presentation.OrderCreateRequest

data class OrderCreateCommand(
    val user: User,
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
        fun toCommand(user: User, request: OrderCreateRequest): OrderCreateCommand {
            return OrderCreateCommand(
                user = user,
                shippingAddress = request.shippingAddress,
                items = request.orderItems.map { Item.of(it) },
            )
        }
    }
}
