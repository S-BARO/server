package com.dh.baro.order.presentation

import com.dh.baro.order.domain.Order
import com.dh.baro.order.domain.OrderStatus
import com.dh.baro.product.domain.Product
import java.math.BigDecimal
import java.time.Instant

data class OrderDetailResponse(
    val orderId: Long,
    val orderStatus: OrderStatus,
    val shippingAddress: String,
    val totalPrice: BigDecimal,
    val orderedAt: Instant?,
    val items: List<Item?>,
) {

    data class Item(
        val productId: Long,
        val productName: String,
        val quantity: Int,
        val priceAtPurchase: BigDecimal,
    )

    companion object {
        fun from(order: Order, productList: List<Product>): OrderDetailResponse {
            val productMapByIds = productList.associateBy { it.id }

            return OrderDetailResponse(
                orderId = order.id,
                orderStatus = order.status,
                shippingAddress = order.shippingAddress,
                totalPrice = order.totalPrice,
                orderedAt = order.createdAt,
                items = order.items.map {
                    productMapByIds[it.productId]?.let { product ->
                        Item(
                            productId = it.productId,
                            productName = product.getName(),
                            quantity = it.quantity,
                            priceAtPurchase = it.priceAtPurchase,
                        )
                    }
                }
            )
        }
    }
}
