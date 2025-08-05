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

    fun createOrder(userId: Long, request: OrderCreateRequest): Order {
        val user = userService.findById(userId)
        val cmd = OrderCreateCommand.toCommand(user, request)
        return orderService.createOrder(cmd)
    }

    fun getOrdersByCursor(
        userId: Long,
        cursorId: Long?,
        size: Int,
    ): Slice<Order> =
        orderService.getOrdersByCursor(userId, cursorId, size)
}
