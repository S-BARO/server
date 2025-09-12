package com.dh.baro.look.domain

import com.dh.baro.core.IdGenerator
import java.time.Instant

data class Swipe(
    val id: Long,
    val userId: Long,
    val lookId: Long,
    val reactionType: ReactionType,
    val createdAt: Instant? = null,
) {

    companion object {
        fun of(
            userId: Long,
            lookId: Long,
            reactionType: ReactionType,
        ): Swipe {
            return Swipe(
                id = IdGenerator.generate(),
                userId = userId,
                lookId = lookId,
                reactionType = reactionType,
            )
        }
    }
}
