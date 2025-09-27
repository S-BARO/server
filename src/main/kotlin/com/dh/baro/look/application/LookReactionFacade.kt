package com.dh.baro.look.application

import com.dh.baro.look.domain.ReactionType
import com.dh.baro.look.domain.service.LookReactionService
import org.springframework.stereotype.Service

@Service
class LookReactionFacade(
    private val lookReactionService: LookReactionService,
) {

    fun recordLookReaction(userId: Long, lookId: Long, reactionType: ReactionType) {
        lookReactionService.upsertLookReaction(
            userId = userId,
            lookId = lookId,
            reactionType = reactionType,
        )
    }
}