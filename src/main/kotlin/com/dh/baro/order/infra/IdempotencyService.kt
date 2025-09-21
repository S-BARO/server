package com.dh.baro.order.infra

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class IdempotencyService(
    private val redisTemplate: RedisTemplate<String, String>,
) {

    fun isAlreadyProcessed(eventId: Long): Boolean {
        val key = buildKey(eventId)
        return redisTemplate.hasKey(key)
    }

    fun tryMarkAsProcessing(eventId: Long): Boolean {
        val key = buildKey(eventId)
        return redisTemplate.opsForValue().setIfAbsent(key, "processing", TTL_DURATION) ?: false
    }

    fun markProcessingAsCompleted(eventId: Long, processingInfo: String = "completed") {
        val key = buildKey(eventId)
        redisTemplate.opsForValue().set(key, processingInfo, TTL_DURATION)
    }

    fun removeProcessingState(eventId: Long) {
        val key = buildKey(eventId)
        redisTemplate.delete(key)
    }

    private fun buildKey(eventId: Long): String {
        return "$IDEMPOTENCY_KEY_PREFIX$eventId"
    }

    companion object {
        private const val IDEMPOTENCY_KEY_PREFIX = "idempotency:"
        private val TTL_DURATION = Duration.ofHours(24)
    }
}
