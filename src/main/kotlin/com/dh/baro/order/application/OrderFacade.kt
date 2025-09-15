package com.dh.baro.order.application

import com.dh.baro.identity.domain.service.UserService
import com.dh.baro.order.domain.Order
import com.dh.baro.order.domain.service.OrderQueryService
import com.dh.baro.order.domain.service.OrderService
import com.dh.baro.order.domain.service.OrderServiceV2
import com.dh.baro.order.presentation.dto.OrderCreateRequest
import com.dh.baro.order.domain.event.OrderPlacedEvent
import com.dh.baro.product.domain.InventoryItem
import com.dh.baro.product.infra.event.InventoryDeductionRequestedEvent
import com.dh.baro.product.domain.service.InventoryService
import com.dh.baro.product.domain.service.ProductQueryService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderFacade(
    private val userService: UserService,
    private val productQueryService: ProductQueryService,
    private val orderService: OrderService,
    private val orderServiceV2: OrderServiceV2,
    private val orderQueryService: OrderQueryService,
    private val inventoryService: InventoryService,
    private val eventPublisher: ApplicationEventPublisher,
) {

    fun placeOrder(userId: Long, request: OrderCreateRequest): Order {
        userService.checkUserExists(userId)
        val productList = productQueryService.getProductsExists(request.orderItems.map { orderItem -> orderItem.productId })
        val cmd = OrderCreateCommand.toCommand(userId, productList, request)
        val order = orderService.createOrder(cmd)
        return order
    }

    @Transactional
    fun placeOrderV2(userId: Long, request: OrderCreateRequest): Order {
        userService.checkUserExists(userId)

        val productList = productQueryService.getProductsExists(
            request.orderItems.map { orderItem -> orderItem.productId },
        )

        val cmd = OrderCreateCommand.toCommand(userId, productList, request)
        val order = orderServiceV2.createOrderV2(cmd)

        val inventoryItems = cmd.orderItems.map { item ->
            InventoryItem(item.product.id, item.quantity)
        }
        inventoryService.deductStocksFromRedis(inventoryItems)

        publishInventoryDeductionEvent(order, cmd)
        return order
    }

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

    private fun publishInventoryDeductionEvent(order: Order, cmd: OrderCreateCommand) {
        val inventoryItems = cmd.orderItems.map { item ->
            InventoryItem(
                productId = item.product.id,
                quantity = item.quantity,
            )
        }

        val event = InventoryDeductionRequestedEvent(
            orderId = order.id,
            userId = cmd.userId,
            items = inventoryItems,
        )

        eventPublisher.publishEvent(event)
    }

    @Transactional
    fun placeOrderV3(userId: Long, request: OrderCreateRequest): Order {
        val productList = productQueryService.getProductsExists(
            request.orderItems.map { orderItem -> orderItem.productId },
        )

        val cmd = OrderCreateCommand.toCommand(userId, productList, request)
        val order = orderServiceV2.createOrderV2(cmd)

        val inventoryItems = cmd.orderItems.map { item ->
            InventoryItem(item.product.id, item.quantity)
        }
        inventoryService.deductStocksFromRedis(inventoryItems)

        val orderPlacedEvent = OrderPlacedEvent(
            orderId = order.id,
            userId = cmd.userId,
            items = inventoryItems
        )
        eventPublisher.publishEvent(orderPlacedEvent)
        return order
    }
}
