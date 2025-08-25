package com.dh.baro.order.application

import com.dh.baro.order.domain.Order
import com.dh.baro.product.domain.Product

data class OrderDetailBundle (
    val order: Order,
    val productList: List<Product>,
)
