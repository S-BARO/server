package com.dh.baro.order.application

import com.dh.baro.core.ErrorMessage
import com.dh.baro.order.domain.Order
import com.dh.baro.order.domain.OrderRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class OrderQueryFacade(
    private val orderRepository: OrderRepository,
) {

    fun getOrderDetail(userId: Long, orderId: Long): Order =
        orderRepository.findOrderByUserIdAndId(userId, orderId)
            ?: throw IllegalArgumentException(ErrorMessage.ORDER_NOT_FOUND.format(orderId))

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
