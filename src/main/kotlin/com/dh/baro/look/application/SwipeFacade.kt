package com.dh.baro.look.application

import com.dh.baro.look.domain.ReactionType
import com.dh.baro.look.domain.service.LookReactionService
import org.springframework.stereotype.Service

@Service
class SwipeFacade(
    private val lookReactionService: LookReactionService,
) {

    fun recordSwipe(userId: Long, lookId: Long, reactionType: ReactionType) {
        lookReactionService.saveLookReaction(
            userId = userId,
            lookId = lookId,
            reactionType = reactionType,
        )
    }

    fun cancelSwipe(userId: Long, lookId: Long) =
        lookReactionService.deleteLookReaction(userId, lookId)
}
