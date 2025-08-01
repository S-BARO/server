package com.dh.baro.preference.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class ProductLikeId(
    @Column(name = "user_id")
    val userId: Long,

    @Column(name = "product_id")
    val productId: Long
) : Serializable
