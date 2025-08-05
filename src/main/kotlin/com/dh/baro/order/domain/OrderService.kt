package com.dh.baro.order.domain

import com.dh.baro.core.ErrorMessage
import com.dh.baro.order.application.OrderCreateCommand
import com.dh.baro.product.domain.ProductRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class OrderService(
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
) {

    @Transactional
    fun createOrder(cmd: OrderCreateCommand): Order {
        val order = Order.newOrder(cmd.user, cmd.shippingAddress)
        cmd.items.forEach { addItemsToOrder(it, order) }
        order.updateTotalPrice()
        return order
    }

    private fun addItemsToOrder(item: OrderCreateCommand.Item, order: Order) {
        val product = productRepository.findByIdForUpdate(item.productId)
            ?: throw IllegalArgumentException(ErrorMessage.PRODUCT_NOT_FOUND.format(item.productId))

        product.deductStockForOrder(item.quantity)

        val orderItem = OrderItem.newOrderItem(
            order    = order,
            product  = product,
            quantity = item.quantity,
        )
        order.addItem(orderItem)
    }

    fun getOrderDetail(userId: Long, orderId: Long): Order =
        orderRepository.findOrderById(orderId)
            ?: throw IllegalArgumentException(ErrorMessage.ORDER_NOT_FOUND.format(orderId))

    fun getOrdersByCursor(
        userId: Long,
        cursorId: Long?,
        size: Int,
    ): Slice<Order> {
        val pageable = PageRequest.of(0, size)
        return orderRepository.findByUserIdAndCursorId(
            userId = userId,
            cursorId = cursorId,
            pageable = pageable,
        )
    }
}
