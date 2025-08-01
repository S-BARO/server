package com.dh.baro.cart.domain

import org.springframework.stereotype.Component

@Component
class CartPolicy {

    companion object {
        const val ITEM_LIMIT = 20
    }

    fun canAddMoreItems(currentCount: Long): Boolean =
        currentCount < ITEM_LIMIT
}
