package com.dh.baro.look.infra.redis

import com.dh.baro.core.ErrorMessage
import com.dh.baro.look.presentation.dto.LookDetailResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Service

@Service
class LookCacheService(
    @Qualifier("lookCacheManager")
    private val cacheManager: CacheManager,
) {

    fun getCachedLookDetail(lookId: Long): LookDetailResponse? {
        return try {
            val cache = getCache()
            val cacheKey = generateCacheKey(lookId)
            val cached = cache.get(cacheKey, LookDetailResponse::class.java)

            if (cached != null && cached.lookId == -1L) {
                null
            } else {
                cached
            }
        } catch (e: Exception) {
            null
        }
    }

    fun cacheLookDetail(lookId: Long, response: LookDetailResponse) {
        try {
            val cache = getCache()
            val cacheKey = generateCacheKey(lookId)
            cache.put(cacheKey, response)
        } catch (e: Exception) {
            // Silent fallback
        }
    }

    fun cacheEmptyLookDetail(lookId: Long) {
        try {
            val cache = getCache()
            val cacheKey = generateCacheKey(lookId)
            cache.put(cacheKey, LookDetailResponse.EMPTY)
        } catch (e: Exception) {
            // Silent fallback
        }
    }

    fun evictLookDetail(lookId: Long) {
        try {
            val cache = getCache()
            val cacheKey = generateCacheKey(lookId)
            cache.evict(cacheKey)
        } catch (e: Exception) {
            // Silent fallback
        }
    }

    private fun getCache(): Cache {
        return cacheManager.getCache(CACHE_NAME)
            ?: throw IllegalStateException(ErrorMessage.CACHE_NOT_FOUND.format(CACHE_NAME))
    }

    private fun generateCacheKey(lookId: Long): String {
        return "$CACHE_KEY_PREFIX:${lookId}"
    }

    companion object {
        private const val CACHE_NAME = "detail"
        private const val CACHE_KEY_PREFIX = "look"
    }
}
