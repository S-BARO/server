package com.dh.baro.order.application

import com.dh.baro.identity.domain.service.UserService
import com.dh.baro.order.domain.Order
import com.dh.baro.order.domain.OrderQueryService
import com.dh.baro.order.domain.OrderService
import com.dh.baro.order.presentation.dto.OrderCreateRequest
import com.dh.baro.product.domain.service.ProductQueryService
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service

@Service
class OrderFacade(
    private val userService: UserService,
    private val productQueryService: ProductQueryService,
    private val orderService: OrderService,
    private val orderQueryService: OrderQueryService,
) {

    fun placeOrder(userId: Long, request: OrderCreateRequest): Order {
        userService.checkUserExists(userId)
        val productList = productQueryService.getProductsExists(request.orderItems.map { orderItem -> orderItem.productId })
        val cmd = OrderCreateCommand.toCommand(userId, productList, request)
        val order = orderService.createOrder(cmd)
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
}
