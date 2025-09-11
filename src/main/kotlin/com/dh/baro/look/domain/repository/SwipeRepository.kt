package com.dh.baro.look.domain.repository

import com.dh.baro.look.domain.Swipe

interface SwipeRepository {

    fun upsert(swipe: Swipe): Swipe

    fun existsByUserIdAndLookId(userId: Long, lookId: Long): Boolean

    fun findByUserIdAndLookId(userId: Long, lookId: Long): Swipe?

    fun findUserSwipeHistory(userId: Long): List<Swipe>

    fun deleteByUserIdAndLookId(userId: Long, lookId: Long): Long

    fun deleteLikeIfExists(userId: Long, lookId: Long): Int
}
