package com.dh.baro.look.domain.service

import com.dh.baro.core.ErrorMessage
import com.dh.baro.core.exception.TooManyRequestsException
import com.dh.baro.look.domain.repository.CreditRepository
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class CreditService(
    private val creditRepository: CreditRepository,
    private val redissonClient: RedissonClient,
) {

    fun executeWithCreditCheck(userId: Long, action: () -> Unit) {
        // 분산락 획득 후 크레딧 검증 및 차감 실행
        withLock(userId) {
            validateAndDeductCredit(userId, action)
        }
    }

    private fun withLock(userId: Long, block: () -> Unit) {
        val lock = redissonClient.getLock("$LOCK_KEY_PREFIX$userId")

        // 락 획득 시도 (최대 3초 대기, Watchdog 자동 활성화)
        val acquired = runCatching {
            lock.tryLock(LOCK_WAIT_TIME, TimeUnit.SECONDS)
        }.getOrElse {
            throw IllegalStateException(ErrorMessage.AI_FITTING_TOKEN_BUCKET_ERROR.message)
        }

        if (!acquired) {
            throw TooManyRequestsException(ErrorMessage.AI_FITTING_RATE_LIMIT_EXCEEDED.message)
        }

        try {
            block()
        } finally {
            // 락 해제 (Watchdog도 함께 중지)
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
            }
        }
    }

    private fun validateAndDeductCredit(userId: Long, action: () -> Unit) {
        if (!creditRepository.checkCreditAvailability(userId)) {
            throw TooManyRequestsException(ErrorMessage.AI_FITTING_RATE_LIMIT_EXCEEDED.message)
        }

        // action 실행 후 성공 시 크레딧 차감
        runCatching {
            action()
        }.onSuccess {
            if (!creditRepository.deductCredit(userId)) {
                throw IllegalStateException(ErrorMessage.AI_FITTING_RATE_LIMIT_EXCEEDED.message)
            }
        }.onFailure { e ->
            throw e
        }
    }

    companion object {
        private const val LOCK_KEY_PREFIX = "credit:lock:"
        private const val LOCK_WAIT_TIME = 3L
    }
}
