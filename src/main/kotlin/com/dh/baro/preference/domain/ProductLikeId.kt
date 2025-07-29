package com.dh.baro.preference.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class ProductLikeId(
    @Column(name = "member_id")
    val memberId: Long,

    @Column(name = "product_id")
    val productId: Long
) : Serializable
