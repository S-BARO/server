package com.dh.baro.cart.infra

import com.dh.baro.cart.domain.CartService
import com.dh.baro.order.application.event.OrderPlacedEvent
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class CartEventListener(
    private val cartService: CartService,
) {

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun removeCartItemsAfterOrder(event: OrderPlacedEvent) {
        val orderItemsMap = event.items.associate { it.productId to it.quantity }
        cartService.removeItems(event.userId, orderItemsMap)
    }
}
