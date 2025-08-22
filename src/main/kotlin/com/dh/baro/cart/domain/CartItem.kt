package com.dh.baro.cart.domain

import com.dh.baro.core.BaseTimeEntity
import com.dh.baro.core.IdGenerator
import com.dh.baro.identity.domain.User
import com.dh.baro.product.domain.Product
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product,

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
        fun newCartItem(user: User, product: Product, quantity: Int): CartItem {
            return CartItem(
                id = IdGenerator.generate(),
                user = user,
                product = product,
                quantity = quantity,
            )
        }
    }
}
