package com.dh.baro.look.domain.service

import com.dh.baro.look.domain.LookReaction
import com.dh.baro.look.domain.ReactionType
import com.dh.baro.look.domain.repository.LookReactionRepository
import com.dh.baro.look.domain.repository.LookRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LookReactionService(
    private val lookRepository: LookRepository,
    private val lookReactionRepository: LookReactionRepository,
) {

    @Transactional
    fun createReactionIfAbsent(userId: Long, lookId: Long, reactionType: ReactionType) {
        runCatching {
            saveReaction(userId, lookId, reactionType)
        }.onFailure { exception ->
            when {
                exception is DataIntegrityViolationException -> {
                    updateReaction(userId, lookId, reactionType)
                }
                else -> throw exception
            }
        }
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

    private fun updateReaction(userId: Long, lookId: Long, reactionType: ReactionType) {
        lookReactionRepository.findByUserIdAndLookId(userId, lookId)
            ?.changeReactionType(reactionType)
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
