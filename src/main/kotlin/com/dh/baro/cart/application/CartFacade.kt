package com.dh.baro.cart.application

import com.dh.baro.cart.domain.CartItem
import com.dh.baro.cart.domain.CartService
import com.dh.baro.cart.presentation.dto.AddItemRequest
import org.springframework.stereotype.Service

@Service
class CartFacade(
    private val cartService: CartService,
) {

    fun getCartItems(userId: Long): List<CartItem> =
        cartService.getItems(userId)

    fun addItem(userId: Long, request: AddItemRequest) {
        cartService.addItem(
            userId = userId,
            productId = request.productId,
            quantity = request.quantity,
        )
    }

    fun updateQuantity(userId: Long, itemId: Long, qty: Int) =
        cartService.updateItemQuantity(userId, itemId, qty)

    fun removeItem(userId: Long, itemId: Long) =
        cartService.removeItem(userId, itemId)
}
