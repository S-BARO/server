package com.dh.baro.look.domain.repository

import com.dh.baro.look.domain.Look
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.*
import org.springframework.data.repository.query.Param

interface LookRepository : JpaRepository<Look, Long> {

    @EntityGraph(attributePaths = ["images", "products"])
    @Query("select l from Look l where l.id = :id")
    fun findWithImagesAndProductsById(@Param("id") id: Long): Look?

    @Query("""
        select l from Look l
        where (:lookIdsEmpty = true or l.id not in :lookIds)
        and (:cursorId is null or l.id < :cursorId)
        order by l.id desc
    """)
    fun findLooksForSwipe(
        @Param("cursorId") cursorId: Long?,
        @Param("lookIds") lookIds: List<Long>,
        @Param("lookIdsEmpty") lookIdsEmpty: Boolean,
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
