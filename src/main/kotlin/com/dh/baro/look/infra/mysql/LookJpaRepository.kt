package com.dh.baro.look.infra.mysql

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface LookJpaRepository : JpaRepository<LookEntity, Long> {

    @EntityGraph(attributePaths = ["images", "products"])
    @Query("select l from LookEntity l where l.id = :id")
    fun findWithImagesAndProductsById(@Param("id") id: Long): LookEntity?

    @Query("""
        select l from LookEntity l
        where l.id not in :lookIds
        and (:cursorId is null or l.id < :cursorId)
        order by l.id desc
    """)
    fun findLooksForSwipe(
        @Param("userId") userId: Long,
        @Param("cursorId") cursorId: Long?,
        @Param("lookIds") lookIds: List<Long>,
        pageable: Pageable,
    ): Slice<LookEntity>

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        """
        UPDATE LookEntity l 
        SET l.likesCount = l.likesCount + 1 
        WHERE l.id = :id
    """
    )
    fun incrementLikesCount(@Param("id") id: Long): Int

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        """
        UPDATE LookEntity l 
        SET l.likesCount = l.likesCount - 1 
        WHERE l.id = :id AND l.likesCount > 0
    """
    )
    fun decrementLikesCount(@Param("id") id: Long): Int
}
