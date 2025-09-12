package com.dh.baro.look.domain.service

import com.dh.baro.core.ErrorMessage
import com.dh.baro.look.application.LookCreateCommand
import com.dh.baro.look.domain.Look
import com.dh.baro.look.domain.ReactionType
import com.dh.baro.look.domain.repository.LookRepository
import com.dh.baro.look.domain.repository.SwipeRepository
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class LookService(
    private val lookRepository: LookRepository,
    private val swipeRepository: SwipeRepository,
) {

    @Transactional
    fun createLook(cmd: LookCreateCommand): Look {
        val look = Look.newLook(
            creatorId = cmd.creatorId,
            title = cmd.title,
            description = cmd.description,
            thumbnailUrl = cmd.thumbnailUrl,
            imageUrls = cmd.imageUrls,
            productIds = cmd.productIds,
        )

        return lookRepository.save(look)
    }

    fun getLooksForSwipe(userId: Long, cursorId: Long?, size: Int): Slice<Look> {
        val lookIds = swipeRepository.findLookIdsByUserId(userId)
        return lookRepository.findLooksForSwipe(userId, cursorId, lookIds, size)
    }

    fun getLookDetail(lookId: Long): Look =
        lookRepository.findWithImagesAndProductsById(lookId)
            ?: throw IllegalArgumentException(ErrorMessage.LOOK_NOT_FOUND.format(lookId))

    @Transactional
    fun incrementLikeCountIfNeeded(type: ReactionType, lookId: Long) {
        if (type == ReactionType.LIKE) {
            lookRepository.incrementLike(lookId)
        }
    }
}
