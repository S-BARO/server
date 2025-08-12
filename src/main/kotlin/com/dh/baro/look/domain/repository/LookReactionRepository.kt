package com.dh.baro.look.domain.repository

import com.dh.baro.look.domain.LookReaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface LookReactionRepository : JpaRepository<LookReaction, Long> {
    fun existsByUserIdAndLookId(userId: Long, lookId: Long): Boolean

    fun deleteByUserIdAndLookId(userId: Long, lookId: Long): Long

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        delete from LookReaction r
        where r.userId = :userId 
            and r.lookId = :lookId 
            and r.reactionType = 'LIKE'
    """)
    fun deleteLikeIfExists(userId: Long, lookId: Long): Int
}
