package com.dh.baro.look.domain.repository

import com.dh.baro.look.domain.LookReaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface LookReactionRepository : JpaRepository<LookReaction, Long> {

    fun findByUserIdAndLookId(userId: Long, lookId: Long): LookReaction?

    fun deleteByUserIdAndLookId(userId: Long, lookId: Long): Int

    @Query("SELECT lr.lookId FROM LookReaction lr WHERE lr.userId = :userId")
    fun findLookIdsByUserId(@Param("userId") userId: Long): List<Long>

    @Query("""
        SELECT lr FROM LookReaction lr
        WHERE lr.userId = :userId
        AND lr.reactionType = 'LIKE'
        AND (:cursorId IS NULL OR lr.id < :cursorId)
        ORDER BY lr.id DESC
    """)
    fun findLikedReactionsByUserId(
        @Param("userId") userId: Long,
        @Param("cursorId") cursorId: Long?,
        pageable: org.springframework.data.domain.Pageable,
    ): org.springframework.data.domain.Slice<LookReaction>
}
