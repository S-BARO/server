package com.dh.baro.order.domain

import com.dh.baro.core.ErrorMessage
import com.dh.baro.order.application.OrderCreateCommand
import com.dh.baro.product.domain.ProductRepository
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
        val order = Order.newOrder(cmd.userId, cmd.shippingAddress)

        val mergedItems = mergeDuplicateItems(cmd.items)
        mergedItems.forEach { addItemsToOrder(it, order) }
        order.updateTotalPrice()

        return orderRepository.save(order)
    }

    private fun mergeDuplicateItems(
        items: List<OrderCreateCommand.Item>
    ): List<OrderCreateCommand.Item> =
        items
            .groupBy { it.productId }
            .map { (productId, group) ->
                val totalQty = group.sumOf { it.quantity }
                OrderCreateCommand.Item(productId, totalQty)
            }

    private fun addItemsToOrder(item: OrderCreateCommand.Item, order: Order) {
        val product = productRepository.findByIdForUpdate(item.productId)
            ?: throw IllegalArgumentException(ErrorMessage.PRODUCT_NOT_FOUND.format(item.productId))

        product.deductStockForOrder(item.quantity)

        val orderItem = OrderItem.newOrderItem(
            order = order,
            product = product,
            quantity = item.quantity,
        )
        order.addItem(orderItem)
    }
}
