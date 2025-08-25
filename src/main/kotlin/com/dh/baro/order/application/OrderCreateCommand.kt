package com.dh.baro.order.application

import com.dh.baro.order.presentation.OrderCreateRequest
import com.dh.baro.product.domain.Product

data class OrderCreateCommand(
    val userId: Long,
    val productList: List<Product>,
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
        fun toCommand(userId: Long, productList: List<Product>, request: OrderCreateRequest): OrderCreateCommand {
            return OrderCreateCommand(
                userId = userId,
                productList = productList,
                shippingAddress = request.shippingAddress,
                items = request.orderItems.map { Item.of(it) },
            )
        }
    }
}
