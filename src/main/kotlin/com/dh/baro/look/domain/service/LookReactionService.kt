package com.dh.baro.look.domain.service

import com.dh.baro.look.domain.LookReaction
import com.dh.baro.look.domain.ReactionType
import com.dh.baro.look.domain.repository.LookReactionRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class LookReactionService(
    private val lookReactionRepository: LookReactionRepository,
) {

    fun getLookIdsByUserId(userId: Long): List<Long> {
        return lookReactionRepository.findLookIdsByUserId(userId)
    }

    @Transactional
    fun upsertLookReaction(userId: Long, lookId: Long, reactionType: ReactionType) {
        val lookReaction = LookReaction.of(
            userId = userId,
            lookId = lookId,
            reactionType = reactionType,
        )
        lookReactionRepository.save(lookReaction)
    }

    @Transactional
    fun deleteLookReaction(userId: Long, lookId: Long) {
        lookReactionRepository.deleteByUserIdAndLookId(userId, lookId)
    }
}