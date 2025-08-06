package com.dh.baro.cart.domain

import com.dh.baro.core.ErrorMessage
import com.dh.baro.identity.domain.repository.UserRepository
import com.dh.baro.product.domain.repository.ProductRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CartService(
    private val cartItemRepository: CartItemRepository,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val cartPolicy: CartPolicy,
) {

    @Transactional(readOnly = true)
    fun getItems(userId: Long): List<CartItem> =
        cartItemRepository.findByUserId(userId)

    @Transactional
    fun addItem(userId: Long, productId: Long, quantity: Int): CartItem {
        cartItemRepository.findByUserIdAndProductId(userId, productId)
            ?.also { it.addQuantity(quantity); return it }

        validateCartLimit(userId)

        val user = userRepository.findByIdOrNull(userId)
            ?: throw IllegalArgumentException(ErrorMessage.USER_NOT_FOUND.format(userId))
        val product = productRepository.findByIdOrNull(productId)
            ?: throw IllegalArgumentException(ErrorMessage.PRODUCT_NOT_FOUND.format(productId))

        return cartItemRepository.save(CartItem.newCartItem(user, product, quantity))
    }

    private fun validateCartLimit(userId: Long) {
        val currentCnt = cartItemRepository.countByUserId(userId)
        if (!cartPolicy.canAddMoreItems(currentCnt)) {
            throw IllegalStateException(ErrorMessage.CART_ITEM_LIMIT_EXCEEDED.message)
        }
    }

    @Transactional
    fun updateItemQuantity(userId: Long, itemId: Long, newQuantity: Int) {
        val item = cartItemRepository.findByIdAndUserId(itemId, userId)
            ?: throw IllegalArgumentException(ErrorMessage.CART_ITEM_NOT_FOUND.format(itemId))
        item.changeQuantity(newQuantity)
    }

    @Transactional
    fun removeItem(userId: Long, itemId: Long) {
        val item = cartItemRepository.findByIdAndUserId(itemId, userId)
            ?: throw IllegalArgumentException(ErrorMessage.CART_ITEM_NOT_FOUND.format(itemId))
        cartItemRepository.delete(item)
    }
}
