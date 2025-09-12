package com.dh.baro.look.domain

import com.dh.baro.core.IdGenerator
import java.time.Instant

data class LookImage(
    val id: Long,
    val imageUrl: String,
    val displayOrder: Int,
    val createdAt: Instant? = null,
    val modifiedAt: Instant? = null,
) {

    companion object {
        fun of(
            imageUrl: String,
            displayOrder: Int,
        ): LookImage =
            LookImage(
                id = IdGenerator.generate(),
                imageUrl = imageUrl,
                displayOrder = displayOrder,
            )
    }
}
