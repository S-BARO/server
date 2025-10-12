package com.dh.baro.order.application

import com.dh.baro.order.domain.Order
import com.dh.baro.order.domain.service.OrderQueryService
import com.dh.baro.order.domain.service.OrderService
import com.dh.baro.order.presentation.dto.OrderCreateRequest
import com.dh.baro.order.application.event.OrderPlacedEvent
import com.dh.baro.product.application.event.RedisStockDeductionEvent
import com.dh.baro.product.domain.InventoryItem
import com.dh.baro.product.domain.service.ProductQueryService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderFacade(
    private val productQueryService: ProductQueryService,
    private val orderService: OrderService,
    private val orderQueryService: OrderQueryService,
    private val eventPublisher: ApplicationEventPublisher,
) {

    fun getOrderDetail(userId: Long, orderId: Long): Order =
        orderQueryService.getOrderDetailByUserId(orderId, userId)

    fun getOrdersByCursor(
        userId: Long,
        cursorId: Long?,
        size: Int,
    ): Slice<Order> {
        return orderQueryService.getOrdersByCursor(
            userId = userId,
            cursorId = cursorId,
            size = size,
        )
    }

    @Transactional
    fun placeOrder(userId: Long, request: OrderCreateRequest): Order {
        val productList = productQueryService.getProductsExists(
            request.orderItems.map { orderItem -> orderItem.productId },
        )

        val cmd = OrderCreateCommand.toCommand(userId, productList, request)
        val order = orderService.createOrder(cmd)

        val inventoryItems = cmd.orderItems.map { item ->
            InventoryItem(item.product.id, item.quantity)
        }

        val orderPlacedEvent = OrderPlacedEvent(
            orderId = order.id,
            userId = cmd.userId,
            items = inventoryItems,
        )
        eventPublisher.publishEvent(orderPlacedEvent)

        val redisStockDeductionEvent = RedisStockDeductionEvent(
            orderId = order.id,
            items = inventoryItems,
        )
        eventPublisher.publishEvent(redisStockDeductionEvent)

        return order
    }
}
