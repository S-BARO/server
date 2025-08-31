package com.dh.baro.core.outbox

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface OutboxMessageRepository : JpaRepository<OutboxMessage, Long> {

    @Query("""
        SELECT m FROM OutboxMessage m 
        WHERE m.status IN ('INIT', 'SEND_FAIL')
        ORDER BY m.id ASC
    """)
    fun findPendingAndRetryableMessages(pageable: Pageable): List<OutboxMessage>
}
