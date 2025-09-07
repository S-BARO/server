package com.dh.baro.order.presentation.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class OrderCreateRequest(
    @field:NotBlank(message = "배송지를 입력하세요.")
    @field:Size(max = 500, message = "배송지는 최대 500자까지 가능합니다.")
    val shippingAddress: String,

    @field:Size(min = 1, message = "주문 항목이 비어 있습니다.")
    @field:Valid
    val orderItems: List<OrderItem>,
) {

    data class OrderItem(
        @field:NotBlank(message = "상품 ID를 입력해주세요.")
        val productId: String,

        @field:Positive(message = "수량은 1개 이상이어야 합니다.")
        val quantity: Int,
    ) {
        fun toLongProductId() = productId.toLong()
    }
    
    fun convertToLongIds() = OrderCreateRequest(
        shippingAddress = shippingAddress,
        orderItems = orderItems.map { OrderItem(it.productId.toLong().toString(), it.quantity) }
    )
}
