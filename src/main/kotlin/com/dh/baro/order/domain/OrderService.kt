package com.dh.baro.order.domain

import com.dh.baro.core.ErrorMessage
import com.dh.baro.core.exception.ConflictException
import com.dh.baro.order.application.OrderCreateCommand
import com.dh.baro.product.domain.Product
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
        val productMapById = cmd.productList.associateBy { it.id }

        val mergedItems = mergeDuplicateItems(cmd.items)
        mergedItems.forEach { addItemsToOrder(it, order, productMapById) }
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

    private fun addItemsToOrder(item: OrderCreateCommand.Item, order: Order, productMapById: Map<Long, Product>) {
        val product = productMapById[item.productId]
            ?: throw IllegalArgumentException(ErrorMessage.PRODUCT_NOT_FOUND.format(item.productId))

        val updated = productRepository.deductStock(item.productId, item.quantity)

        require(updated != 0) {
            throw ConflictException(ErrorMessage.OUT_OF_STOCK.format(item.productId))
        }

        val orderItem = OrderItem.newOrderItem(
            order = order,
            productId = item.productId,
            quantity = item.quantity,
            priceAtPurchase = product.getPrice(),
        )
        order.addItem(orderItem)
    }
}
