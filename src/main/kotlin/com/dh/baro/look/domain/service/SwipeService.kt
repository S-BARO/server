package com.dh.baro.look.domain.service

import com.dh.baro.look.domain.Swipe
import com.dh.baro.look.domain.ReactionType
import com.dh.baro.look.domain.repository.SwipeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SwipeService(
    private val swipeRepository: SwipeRepository,
) {

    @Transactional
    fun upsertSwipe(userId: Long, lookId: Long, reactionType: ReactionType) {
        val swipe = Swipe.of(
            userId = userId,
            lookId = lookId,
            reactionType = reactionType,
        )
        swipeRepository.upsertSwipe(swipe)
    }

    @Transactional
    fun deleteSwipe(userId: Long, lookId: Long) {
        val likeDeleted = swipeRepository.deleteLikeIfExists(userId, lookId) > 0
        if (likeDeleted) {
            // todo: 좋아요 제거 이벤트 발급
        } else {
            swipeRepository.deleteByUserIdAndLookId(userId, lookId)
        }
    }
}
