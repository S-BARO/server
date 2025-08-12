package com.dh.baro.look.domain.repository

import com.dh.baro.look.domain.Look
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.*
import org.springframework.data.repository.query.Param

interface LookRepository : JpaRepository<Look, Long> {

    @Query("""
        select l from Look l
        where l.id not in (
            select lr.lookId from LookReaction lr where lr.userId = :userId
        )
        and (:cursorId is null or l.id < :cursorId)
        order by l.id desc
    """)
    fun findSwipeLooks(
        @Param("userId") userId: Long,
        @Param("cursorId") cursorId: Long?,
        pageable: Pageable,
    ): Slice<Look>

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Look l 
        set l.likesCount = l.likesCount + 1 
        where l.id = :lookId
    """)
    fun incrementLike(@Param("lookId") lookId: Long): Int

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Look l
        set l.likesCount = l.likesCount - 1
        where l.id = :lookId and l.likesCount > 0
    """)
    fun decrementLike(lookId: Long): Int
}
