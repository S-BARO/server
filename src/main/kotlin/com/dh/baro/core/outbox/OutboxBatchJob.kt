package com.dh.baro.core.outbox

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class OutboxBatchJob(
    private val outboxMessageHandler: OutboxMessageHandler,
    private val outboxMessageRepository: OutboxMessageRepository,
) {

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    fun run() {
        val pageable = PageRequest.of(0, 100)
        val messages = outboxMessageRepository.findPendingAndRetryableMessages(pageable)
        if (messages.isNotEmpty()) {
            processMessages(messages)
        }
    }

    private fun processMessages(messages: List<OutboxMessage>) {
        messages.forEach { msg ->
                outboxMessageHandler.handleInNewTx(msg)
        }
    }
}
