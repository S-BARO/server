package com.dh.baro.look.application

import com.dh.baro.identity.domain.service.UserService
import com.dh.baro.look.domain.ReactionType
import com.dh.baro.look.domain.service.LookReactionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LookReactionFacade(
    private val userService: UserService,
    private val lookReactionService: LookReactionService,
) {

    @Transactional
    fun recordReaction(userId: Long, lookId: Long, reactionType: ReactionType) {
        userService.checkUserExists(userId)
        lookReactionService.createReactionIfAbsent(
            userId = userId,
            lookId = lookId,
            reactionType = reactionType,
        )
    }

    fun cancelReaction(userId: Long, lookId: Long) =
        lookReactionService.deleteReaction(userId, lookId)
}
