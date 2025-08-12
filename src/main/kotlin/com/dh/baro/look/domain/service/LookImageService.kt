package com.dh.baro.look.domain.service

import com.dh.baro.look.domain.LookImage
import com.dh.baro.look.domain.repository.LookImageRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class LookImageService(
    private val lookImageRepository: LookImageRepository,
) {
    fun findByLookIdOrderByDisplay(lookId: Long): List<LookImage> =
        lookImageRepository.findByLookIdOrderByDisplayOrderAsc(lookId)
}
