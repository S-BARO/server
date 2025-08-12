package com.dh.baro.look.domain.service

import com.dh.baro.look.domain.LookReaction
import com.dh.baro.look.domain.ReactionType
import com.dh.baro.look.domain.repository.LookReactionRepository
import com.dh.baro.look.domain.repository.LookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LookReactionService(
    private val lookRepository: LookRepository,
    private val lookReactionRepository: LookReactionRepository,
) {

    @Transactional
    fun createReactionIfAbsent(userId: Long, lookId: Long, reactionType: ReactionType) {
        if (lookReactionRepository.existsByUserIdAndLookId(userId, lookId)) return
        saveReaction(userId, lookId, reactionType)
        incrementLikeCountIfNeeded(reactionType, lookId)
    }

    private fun saveReaction(userId: Long, lookId: Long, reactionType: ReactionType) {
        lookReactionRepository.save(
            LookReaction.of(
                userId = userId,
                lookId = lookId,
                reactionType = reactionType,
            )
        )
    }

    private fun incrementLikeCountIfNeeded(type: ReactionType, lookId: Long) {
        if (type == ReactionType.LIKE) {
            lookRepository.incrementLike(lookId)
        }
    }

    @Transactional
    fun deleteReaction(userId: Long, lookId: Long) {
        val likeDeleted = lookReactionRepository.deleteLikeIfExists(userId, lookId) > 0
        if (likeDeleted) {
            lookRepository.decrementLike(lookId)
        } else {
            lookReactionRepository.deleteByUserIdAndLookId(userId, lookId)
        }
    }
}
