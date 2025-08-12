package com.dh.baro.look.domain.service

import com.dh.baro.core.ErrorMessage
import com.dh.baro.look.application.LookCreateCommand
import com.dh.baro.look.domain.Look
import com.dh.baro.look.domain.repository.LookRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class LookService(
    private val lookRepository: LookRepository,
) {

    @Transactional
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

    fun getSwipeLooks(userId: Long, cursorId: Long?, size: Int): Slice<Look> =
        lookRepository.findSwipeLooks(userId, cursorId, PageRequest.of(0, size))

    fun getLook(lookId: Long): Look =
        lookRepository.findByIdOrNull(lookId)
            ?: throw IllegalArgumentException(ErrorMessage.LOOK_NOT_FOUND.format(lookId))
}
