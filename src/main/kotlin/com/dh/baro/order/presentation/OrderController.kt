package com.dh.baro.order.presentation

import com.dh.baro.core.Cursor
import com.dh.baro.core.SliceResponse
import com.dh.baro.core.anotation.CurrentUser
import com.dh.baro.order.application.OrderFacade
import com.dh.baro.order.application.OrderQueryFacade
import com.dh.baro.order.presentation.swagger.OrderSwagger
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/orders")
class OrderController(
    private val orderQueryFacade: OrderQueryFacade,
    private val orderFacade: OrderFacade,
) : OrderSwagger {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    override fun placeOrder(
        @CurrentUser userId: Long,
        @Valid @RequestBody request: OrderCreateRequest,
    ): OrderDetailResponse {
        val order = orderFacade.placeOrder(userId, request)
        return OrderDetailResponse.from(order)
    }

    @GetMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    override fun getOrderDetail(
        @CurrentUser userId: Long,
        @PathVariable orderId: Long,
    ): OrderDetailResponse =
        OrderDetailResponse.from(orderQueryFacade.getOrderDetail(userId, orderId))

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    override fun getOrdersByCursor(
        @CurrentUser userId: Long,
        @RequestParam(required = false) cursorId: Long?,
        @RequestParam(defaultValue = "10") size: Int,
    ): SliceResponse<OrderListItem> {
        val slice = orderQueryFacade.getOrdersByCursor(userId, cursorId, size)
        return SliceResponse.from(
            slice = slice,
            mapper = OrderListItem::from,
            cursorExtractor = { Cursor(it.id) }
        )
    }
}
