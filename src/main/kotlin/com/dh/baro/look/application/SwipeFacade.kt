package com.dh.baro.look.application

import com.dh.baro.look.domain.ReactionType
import com.dh.baro.look.domain.service.SwipeService
import org.springframework.stereotype.Service

@Service
class SwipeFacade(
    private val swipeService: SwipeService,
) {

    fun recordSwipe(userId: Long, lookId: Long, reactionType: ReactionType) {
        swipeService.upsertSwipe(
            userId = userId,
            lookId = lookId,
            reactionType = reactionType,
        )
    }

    fun cancelSwipe(userId: Long, lookId: Long) =
        swipeService.deleteSwipe(userId, lookId)
}
