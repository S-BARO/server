package com.dh.baro.look.domain.service

import com.dh.baro.look.domain.LookProduct
import com.dh.baro.look.domain.repository.LookProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class LookProductService(
    private val lookProductRepository: LookProductRepository,
) {
    fun findByLookIdOrderByDisplay(lookId: Long): List<LookProduct> =
        lookProductRepository.findByLookIdOrderByDisplayOrderAsc(lookId)
}
