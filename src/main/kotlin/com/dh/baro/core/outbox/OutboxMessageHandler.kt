package com.dh.baro.core.outbox

import com.dh.baro.core.ErrorMessage
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class OutboxMessageHandler(
    private val outboxMessageRouter: OutboxMessageRouter,
    private val outboxMessageRepository: OutboxMessageRepository,
) {
    
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleInNewTx(outboxMessage: OutboxMessage) {
        try {
            outboxMessageRouter.route(outboxMessage)
            outboxMessage.markSendSuccess()
        } catch (exception: Exception) {
            outboxMessage.markSendFail()
        } finally {
            outboxMessageRepository.save(outboxMessage)
            if(outboxMessage.isDeadMessage()){
                log.error(ErrorMessage.OUTBOX_MESSAGE_DEAD.format(outboxMessage.eventType, outboxMessage.id))
            }
        }
    }
}
