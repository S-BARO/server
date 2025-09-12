package com.dh.baro.look.domain

import com.dh.baro.core.IdGenerator
import java.time.Instant

data class LookProduct(
    val id: Long,
    val productId: Long,
    val displayOrder: Int,
    val createdAt: Instant? = null,
    val modifiedAt: Instant? = null,
) {

    companion object {
        fun of(
            productId: Long,
            displayOrder: Int,
        ) = LookProduct(
                id = IdGenerator.generate(),
                productId = productId,
                displayOrder = displayOrder,
            )
    }
}
