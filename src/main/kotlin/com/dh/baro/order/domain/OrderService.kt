package com.dh.baro.order.domain

import com.dh.baro.core.ErrorMessage
import com.dh.baro.core.exception.ConflictException
import com.dh.baro.order.application.OrderCreateCommand
import com.dh.baro.product.domain.repository.ProductRepository
import org.springframework.data.repository.findByIdOrNull
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

        val mergedItems = mergeDuplicateItems(cmd.items)
        mergedItems.forEach { addItemsToOrder(it, order) }
        order.updateTotalPrice()

        return orderRepository.save(order)
    }

    fun createOrderV2(cmd: OrderCreateCommand): Order {
        val order = Order.newOrder(cmd.userId, cmd.shippingAddress)

        val mergedItems = mergeDuplicateItems(cmd.items)
        mergedItems.forEach { addItemsToOrderV2(it, order) }
        order.updateTotalPrice()

        return orderRepository.save(order)
    }

    fun createOrderV3(cmd: OrderCreateCommand): Order {
        val order = Order.newOrder(cmd.userId, cmd.shippingAddress)

        val mergedItems = mergeDuplicateItems(cmd.items)

        val product = productRepository.findByIdOrNull(mergedItems[0].productId)
            ?: throw IllegalArgumentException(ErrorMessage.PRODUCT_NOT_FOUND.format(mergedItems[0].productId))

        val orderItem = OrderItem.newOrderItem(
            order = order,
            product = product,
            quantity = mergedItems[0].quantity,
        )
        order.addItem(orderItem)
        order.updateTotalPrice()

        val saved = orderRepository.save(order)

        val updated = productRepository.deductStock(mergedItems[0].productId, mergedItems[0].quantity)

        if (updated == 0) {
            throw ConflictException(ErrorMessage.OUT_OF_STOCK.format(mergedItems[0].productId))
        }

        return saved
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

    private fun addItemsToOrderV2(item: OrderCreateCommand.Item, order: Order) {
        val updated = productRepository.deductStock(item.productId, item.quantity)

        val product = productRepository.findByIdOrNull(item.productId)
            ?: throw IllegalArgumentException(ErrorMessage.PRODUCT_NOT_FOUND.format(item.productId))

        if (updated == 0) {
            throw ConflictException(ErrorMessage.OUT_OF_STOCK.format(item.productId))
        }

        val orderItem = OrderItem.newOrderItem(
            order = order,
            product = product,
            quantity = item.quantity,
        )
        order.addItem(orderItem)
    }
}
