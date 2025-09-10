package com.dh.baro.cart.domain

import com.dh.baro.core.BaseTimeEntity
import com.dh.baro.core.IdGenerator
import jakarta.persistence.*

@Entity
@Table(
    name = "cart_items",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "product_id"])]
)
class CartItem(
    @Id
    @Column(name = "id")
    val id: Long,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "product_id", nullable = false)
    val productId: Long,

    @Column(name = "quantity", nullable = false)
    var quantity: Int = 1
) : BaseTimeEntity() {

    override fun getId(): Long = id

    fun addQuantity(quantity: Int) {
        this.quantity += quantity
    }

    fun changeQuantity(quantity: Int) {
        this.quantity = quantity
    }

    companion object {
        fun newCartItem(userId: Long, productId: Long, quantity: Int): CartItem {
            return CartItem(
                id = IdGenerator.generate(),
                userId = userId,
                productId = productId,
                quantity = quantity,
            )
        }
    }
}
