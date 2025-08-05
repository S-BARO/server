package com.dh.baro.order.presentation

import com.dh.baro.order.domain.Order
import com.dh.baro.order.domain.OrderStatus
import java.math.BigDecimal
import java.time.Instant

data class OrderDetailResponse(
    val orderId: Long,
    val orderStatus: OrderStatus,
    val shippingAddress: String,
    val totalPrice: BigDecimal,
    val orderedAt: Instant,
    val items: List<Item>,
) {

    data class Item(
        val productId: Long,
        val productName: String,
        val quantity: Int,
        val priceAtPurchase: BigDecimal,
    )

    companion object {
        fun from(order: Order) = OrderDetailResponse(
            orderId = order.id,
            orderStatus = order.status,
            shippingAddress = order.shippingAddress,
            totalPrice = order.totalPrice,
            orderedAt = order.createdAt,
            items = order.items.map {
                Item(
                    productId = it.product.id,
                    productName = it.product.name,
                    quantity = it.quantity,
                    priceAtPurchase = it.priceAtPurchase,
                )
            }
        )
    }
}
