package com.dh.baro.look.domain.service

import com.dh.baro.core.ErrorMessage
import com.dh.baro.core.exception.TooManyRequestsException
import com.dh.baro.look.domain.repository.CreditRepository
import org.springframework.stereotype.Service

@Service
class CreditService(
    private val creditRepository: CreditRepository,
) {

    fun executeWithCreditReservation(userId: Long, action: () -> Unit) {
        // Lua Script로 충전 + 검증 + 차감 진행
        val reserved = creditRepository.reserveCredit(userId)

        if (!reserved) {
            throw TooManyRequestsException(ErrorMessage.AI_FITTING_RATE_LIMIT_EXCEEDED.message)
        }

        // 크레딧 차감 완료, AI 생성 진행
        runCatching {
            action()
        }.onFailure { e ->
            // 실패시 보상 트랜잭션으로 크레딧 환불
            creditRepository.refundCredit(userId)
            throw e
        }
    }
}
