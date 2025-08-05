package com.dh.baro.order.domain

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface OrderRepository : JpaRepository<Order, Long> {

    @Query("""
        select o
        from Order o
        where o.userId = :userId
          and (:cursorId is null or o.id < :cursorId)
        order by o.id desc
    """)
    fun findByUserIdAndCursorId(
        @Param("userId") userId: Long,
        @Param("cursorId") cursorId: Long?,
        pageable: Pageable,
    ): Slice<Order>

    @EntityGraph(attributePaths = ["items", "items.product"])
    fun findOrderById(orderId: Long): Order?
}
