package com.dh.baro.order.domain

import com.dh.baro.core.AbstractTime
import com.dh.baro.core.converter.OrderStatusConverter
import com.dh.baro.identity.domain.Member
import jakarta.persistence.*

@Entity
@Table(name = "orders")
class Order(
    @Id
    @Column(name = "id")
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,

    @Column(name = "total_price", nullable = false)
    var totalPrice: Int,

    @Column(name = "shipping_address", nullable = false, length = 500)
    var shippingAddress: String,

    @Convert(converter = OrderStatusConverter::class)
    @Column(name = "status", nullable = false)
    var status: OrderStatus = OrderStatus.ORDERED,

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true)
    val items: MutableList<OrderItem> = mutableListOf()
) : AbstractTime()
