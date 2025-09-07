package com.dh.baro.order.presentation.dto

import com.dh.baro.order.domain.Order
import com.dh.baro.order.domain.OrderStatus
import java.math.BigDecimal
import java.time.Instant

data class OrderDetailResponse(
    val orderId: String,
    val orderStatus: OrderStatus,
    val shippingAddress: String,
    val totalPrice: BigDecimal,
    val orderedAt: Instant?,
    val items: List<Item>,
) {

    data class Item(
        val productId: String,
        val productName: String,
        val thumbnailUrl: String,
        val quantity: Int,
        val priceAtPurchase: BigDecimal,
    )

    companion object {
        fun from(order: Order): OrderDetailResponse {
            return OrderDetailResponse(
                orderId = order.id.toString(),
                orderStatus = order.status,
                shippingAddress = order.shippingAddress,
                totalPrice = order.totalPrice,
                orderedAt = order.createdAt,
                items = order.items
                    .map { item ->
                        Item(
                            productId = item.productId.toString(),
                            productName = item.name,
                            thumbnailUrl = item.thumbnailUrl,
                            quantity = item.quantity,
                            priceAtPurchase = item.priceAtPurchase,
                        )
                    }
                    .sortedBy { it.productId }
            )
        }
    }
}
