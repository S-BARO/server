package com.dh.baro.cart.application

import com.dh.baro.cart.domain.CartService
import com.dh.baro.cart.presentation.dto.AddItemRequest
import com.dh.baro.core.ErrorMessage
import com.dh.baro.identity.domain.service.StoreService
import com.dh.baro.product.domain.service.ProductQueryService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CartFacade(
    private val cartService: CartService,
    private val productQueryService: ProductQueryService,
    private val storeService: StoreService,
) {

    @Transactional(readOnly = true)
    fun getCartItems(userId: Long): List<CartItemBundle> {
        val cartItems = cartService.getItems(userId)
        val productIds = cartItems.map { it.productId }
        val products = productQueryService.getAllByIds(productIds).associateBy { it.id }

        val storeIds = products.values.map { it.storeId }.toSet()
        val stores = storeService.getStoresByIds(storeIds).associateBy { it.id }

        return cartItems.map { cartItem ->
            val product = products[cartItem.productId]
                ?: throw IllegalStateException(ErrorMessage.PRODUCT_NOT_FOUND.format(cartItem.productId))

            val store = stores[product.storeId]
                ?: throw IllegalStateException(ErrorMessage.STORE_NOT_FOUND.format(product.storeId))

            CartItemBundle(cartItem, product, store)
        }
    }

    @Transactional
    fun addItem(userId: Long, request: AddItemRequest) {
        productQueryService.checkProductsExists(listOf(request.productId))
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
