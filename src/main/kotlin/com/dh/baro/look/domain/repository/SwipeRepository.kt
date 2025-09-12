package com.dh.baro.look.domain.repository

import com.dh.baro.look.domain.Swipe

interface SwipeRepository {

    fun upsertSwipe(swipe: Swipe): Swipe

    fun findLookIdsByUserId(userId: Long): List<Long>

    fun findByUserIdAndLookId(userId: Long, lookId: Long): Swipe?

    fun deleteByUserIdAndLookId(userId: Long, lookId: Long): Long

    fun deleteLikeIfExists(userId: Long, lookId: Long): Int
}
