package com.dh.baro.order.domain

import com.dh.baro.core.ErrorMessage
import com.dh.baro.core.exception.ConflictException
import com.dh.baro.order.application.OrderCreateCommand
import com.dh.baro.product.domain.repository.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class OrderService(
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
) {

    fun createOrder(cmd: OrderCreateCommand): Order {
        val order = Order.newOrder(cmd.userId, cmd.shippingAddress)

        val mergedItems = mergeDuplicateItems(cmd.orderItems)
        mergedItems.forEach { addItemsToOrder(it, order) }
        order.updateTotalPrice()

        return orderRepository.save(order)
    }

    private fun mergeDuplicateItems(
        items: List<OrderCreateCommand.OrderItem>
    ): List<OrderCreateCommand.OrderItem> =
        items
            .groupBy { it.product.id }
            .map { (_, group) ->
                val firstItem = group.first()
                val totalQty = group.sumOf { it.quantity }
                OrderCreateCommand.OrderItem(firstItem.product, totalQty)
            }

    private fun addItemsToOrder(item: OrderCreateCommand.OrderItem, order: Order) {
        val product = item.product

        val updated = productRepository.deductStock(item.product.id, item.quantity)

        if (updated == 0) {
            throw ConflictException(ErrorMessage.OUT_OF_STOCK.format(item.product.id))
        }

        val orderItem = OrderItem.newOrderItem(
            order = order,
            productId = item.product.id,
            name = product.getName(),
            thumbnailUrl = product.getThumbnailUrl(),
            quantity = item.quantity,
            priceAtPurchase = product.getPrice(),
        )
        order.addItem(orderItem)
    }
}
