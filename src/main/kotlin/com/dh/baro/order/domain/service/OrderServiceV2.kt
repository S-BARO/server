package com.dh.baro.order.domain.service

import com.dh.baro.core.ErrorMessage
import com.dh.baro.order.application.OrderCreateCommand
import com.dh.baro.order.domain.Order
import com.dh.baro.order.domain.OrderItem
import com.dh.baro.order.domain.OrderRepository
import com.dh.baro.order.domain.OrderStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderServiceV2(
    private val orderRepository: OrderRepository,
) {

    @Transactional
    fun createOrderV2(cmd: OrderCreateCommand): Order {
        val order = Order.newOrder(cmd.userId, cmd.shippingAddress)
        
        cmd.orderItems.forEach { orderItem ->
            val item = OrderItem.newOrderItem(
                order = order,
                productId = orderItem.product.id,
                name = orderItem.product.getName(),
                thumbnailUrl = orderItem.product.getThumbnailUrl(),
                quantity = orderItem.quantity,
                priceAtPurchase = orderItem.product.getPrice()
            )
            order.addItem(item)
        }
        
        order.updateTotalPrice()
        return orderRepository.save(order)
    }

    @Transactional
    fun changeOrderStatus(orderId: Long, status: OrderStatus): Order {
        val order = orderRepository.findById(orderId)
            .orElseThrow { IllegalArgumentException(ErrorMessage.ORDER_NOT_FOUND.format(orderId)) }
        order.changeStatus(status)
        return orderRepository.save(order)
    }
}
