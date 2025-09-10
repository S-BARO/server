package com.dh.baro.cart.domain

import com.dh.baro.core.ErrorMessage
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CartService(
    private val cartItemRepository: CartItemRepository,
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

        return try {
            cartItemRepository.save(CartItem.newCartItem(userId, productId, quantity))
        } catch (e: DataIntegrityViolationException) {
            val existingItem = cartItemRepository.findByUserIdAndProductId(userId, productId)?: throw e
            existingItem.addQuantity(quantity)
            existingItem
        }
    }

    private fun validateCartLimit(userId: Long) {
        val currentItemCnt = cartItemRepository.countByUserId(userId)
        if (!cartPolicy.canAddMoreItems(currentItemCnt)) {
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
