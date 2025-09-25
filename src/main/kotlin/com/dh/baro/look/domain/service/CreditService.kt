package com.dh.baro.look.domain.service

import com.dh.baro.core.ErrorMessage
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
        val lockKey = "$LOCK_KEY_PREFIX$userId"
        val lock = redissonClient.getLock(lockKey)

        val acquired = try {
            lock.tryLock(LOCK_WAIT_TIME, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            throw IllegalStateException(ErrorMessage.AI_FITTING_TOKEN_BUCKET_ERROR.message)
        }

        if (!acquired) {
            throw IllegalArgumentException(ErrorMessage.AI_FITTING_RATE_LIMIT_EXCEEDED.message)
        }

        try {
            if (!creditRepository.checkCreditAvailability(userId)) {
                throw IllegalArgumentException(ErrorMessage.AI_FITTING_RATE_LIMIT_EXCEEDED.message)
            }

            action()

            if (!creditRepository.deductCredit(userId)) {
                throw IllegalStateException(ErrorMessage.AI_FITTING_RATE_LIMIT_EXCEEDED.message)
            }

        } finally {
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
            }
        }
    }

    companion object {
        private const val LOCK_KEY_PREFIX = "credit:lock:"
        private const val LOCK_WAIT_TIME = 3L
    }
}
