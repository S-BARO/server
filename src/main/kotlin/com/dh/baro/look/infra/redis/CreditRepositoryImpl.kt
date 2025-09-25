package com.dh.baro.look.infra.redis

import com.dh.baro.core.ErrorMessage
import com.dh.baro.look.domain.repository.CreditRepository
import org.redisson.api.RScript
import org.redisson.api.RedissonClient
import org.redisson.client.codec.StringCodec
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class CreditRepositoryImpl(
    private val redissonClient: RedissonClient,
) : CreditRepository {

    private val creditCheckScript: String by lazy {
        ClassPathResource(CREDIT_CHECK_SCRIPT_PATH).inputStream.bufferedReader().use { it.readText() }
    }

    private val creditDeductScript: String by lazy {
        ClassPathResource(CREDIT_DEDUCT_SCRIPT_PATH).inputStream.bufferedReader().use { it.readText() }
    }

    override fun checkCreditAvailability(userId: Long): Boolean {
        val key = "$KEY_PREFIX$userId"
        val currentTimestamp = Instant.now().epochSecond

        return try {
            val script = redissonClient.getScript(StringCodec.INSTANCE)
            val result = script.eval<Long>(
                RScript.Mode.READ_WRITE,
                creditCheckScript,
                RScript.ReturnType.INTEGER,
                listOf<Any>(key),
                currentTimestamp
            )
            result == 1L
        } catch (e: Exception) {
            throw IllegalStateException(ErrorMessage.REDIS_OPERATION_ERROR.message, e)
        }
    }

    override fun deductCredit(userId: Long): Boolean {
        val key = "$KEY_PREFIX$userId"
        val currentTimestamp = Instant.now().epochSecond

        return try {
            val script = redissonClient.getScript(StringCodec.INSTANCE)
            val result = script.eval<Long>(
                RScript.Mode.READ_WRITE,
                creditDeductScript,
                RScript.ReturnType.INTEGER,
                listOf<Any>(key),
                currentTimestamp
            )
            result == 1L
        } catch (e: Exception) {
            throw IllegalStateException(ErrorMessage.REDIS_OPERATION_ERROR.message, e)
        }
    }

    companion object {
        private const val KEY_PREFIX = "img:credit:"
        private const val CREDIT_CHECK_SCRIPT_PATH = "scripts/credit_check.lua"
        private const val CREDIT_DEDUCT_SCRIPT_PATH = "scripts/credit_deduct.lua"
    }
}
