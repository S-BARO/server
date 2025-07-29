package com.dh.baro.preference.domain

import com.dh.baro.core.AbstractTime
import com.dh.baro.identity.domain.Member
import com.dh.baro.product.domain.Product
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table

@Entity
@Table(name = "product_likes")
class ProductLike(
    @EmbeddedId
    val id: ProductLikeId,

    @MapsId("memberId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    val member: Member,

    @MapsId("productId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    val product: Product
) : AbstractTime()
