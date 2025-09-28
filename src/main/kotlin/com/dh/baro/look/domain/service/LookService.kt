package com.dh.baro.look.domain.service

import com.dh.baro.core.ErrorMessage
import com.dh.baro.look.application.dto.LookCreateCommand
import com.dh.baro.look.domain.Look
import com.dh.baro.look.domain.ReactionType
import com.dh.baro.look.domain.repository.LookRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service

@Service
class LookService(
    private val lookRepository: LookRepository,
) {

    fun checkLookExists(lookId: Long) {
        require(lookRepository.existsById(lookId)) {
            ErrorMessage.LOOK_NOT_FOUND.format(lookId)
        }
    }

    fun createLook(cmd: LookCreateCommand): Look {
        val look = Look.newLook(
            creatorId = cmd.creatorId,
            title = cmd.title,
            description = cmd.description,
            thumbnailUrl = cmd.thumbnailUrl,
        )
        look.addImages(cmd.imageUrls)
        look.addProducts(cmd.productIds)

        return lookRepository.save(look)
    }

    fun getLooksForSwipe(lookIds: List<Long>, cursorId: Long?, size: Int): Slice<Look> =
        lookRepository.findLooksForSwipe(
            cursorId = cursorId,
            lookIds = lookIds,
            lookIdsEmpty = lookIds.isEmpty(),
            pageable = PageRequest.of(0, size)
        )

    fun getLookDetail(lookId: Long): Look =
        lookRepository.findWithImagesAndProductsById(lookId)
            ?: throw IllegalArgumentException(ErrorMessage.LOOK_NOT_FOUND.format(lookId))

    fun incrementLikeCountIfNeeded(type: ReactionType, lookId: Long) {
        if (type == ReactionType.LIKE) {
            lookRepository.incrementLike(lookId)
        }
    }
}
