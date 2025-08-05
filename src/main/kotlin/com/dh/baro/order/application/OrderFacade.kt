package com.dh.baro.order.application

import com.dh.baro.identity.domain.service.UserService
import com.dh.baro.order.domain.Order
import com.dh.baro.order.domain.OrderService
import com.dh.baro.order.presentation.OrderCreateRequest
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service

@Service
class OrderFacade(
    private val orderService: OrderService,
    private val userService: UserService,
) {

    fun placeOrder(userId: Long, request: OrderCreateRequest): Order {
        userService.getUserById(userId)
        val cmd = OrderCreateCommand.toCommand(userId, request)
        return orderService.createOrder(cmd)
    }

    fun getOrdersByCursor(
        userId: Long,
        cursorId: Long?,
        size: Int,
    ): Slice<Order> =
        orderService.getOrdersByCursor(userId, cursorId, size)
}
