package com.dh.baro.order.application

import com.dh.baro.order.presentation.dto.OrderCreateRequest
import com.dh.baro.product.domain.Product

data class OrderCreateCommand(
    val userId: Long,
    val shippingAddress: String,
    val orderItems: List<OrderItem>,
) {

    data class OrderItem(
        val product: Product,
        val quantity: Int,
    )

    companion object {
        fun toCommand(userId: Long, productList: List<Product>, request: OrderCreateRequest): OrderCreateCommand {
            val productMap = productList.associateBy { it.id }
            
            val orderItems = request.orderItems.map { requestItem ->
                val product = productMap[requestItem.productId.toLong()]
                    ?: throw IllegalArgumentException("상품을 찾을 수 없습니다: ${requestItem.productId}")
                
                OrderItem(
                    product = product,
                    quantity = requestItem.quantity
                )
            }
            
            return OrderCreateCommand(
                userId = userId,
                shippingAddress = request.shippingAddress,
                orderItems = orderItems
            )
        }
    }
}
