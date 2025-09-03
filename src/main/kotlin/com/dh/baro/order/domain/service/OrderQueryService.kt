package com.dh.baro.order.domain.service

import com.dh.baro.core.ErrorMessage
import com.dh.baro.order.domain.Order
import com.dh.baro.order.domain.OrderRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class OrderQueryService(
    private val orderRepository: OrderRepository,
) {

    fun getOrderDetailByUserId(orderId: Long, userId: Long): Order {
        return orderRepository.findByIdAndUserId(orderId, userId)
            ?: throw IllegalArgumentException(ErrorMessage.ORDER_NOT_FOUND.format(orderId))
    }

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
