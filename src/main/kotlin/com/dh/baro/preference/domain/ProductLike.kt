package com.dh.baro.preference.domain

import com.dh.baro.core.AbstractTime
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "product_likes")
class ProductLike(
    @EmbeddedId
    val id: ProductLikeId
) : AbstractTime()
