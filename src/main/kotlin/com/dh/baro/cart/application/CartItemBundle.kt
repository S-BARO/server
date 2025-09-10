package com.dh.baro.cart.application

import com.dh.baro.cart.domain.CartItem
import com.dh.baro.product.domain.Product

data class CartItemBundle(
    val cartItem: CartItem,
    val product: Product,
)
