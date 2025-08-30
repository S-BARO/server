package com.dh.baro.order.infra.event

import com.dh.baro.core.ErrorMessage
import com.dh.baro.core.event.EventSerializer
import com.dh.baro.core.outbox.OutboxMessage
import com.dh.baro.core.outbox.OutboxMessageRepository
import com.dh.baro.core.outbox.OutboxMessageRouter.Companion.INVENTORY_DEDUCTION_EVENT
import com.dh.baro.product.infra.event.InventoryDeductionRequestedEvent
import com.dh.baro.product.infra.redis.InventoryRedisRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class OrderInventoryEventListener(
    private val eventSerializer: EventSerializer,
    private val outboxMessageRepository: OutboxMessageRepository,
    private val inventoryRedisRepository: InventoryRedisRepository,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun recordInventoryDeductionEvent(event: InventoryDeductionRequestedEvent) {
        try {
            val payload = eventSerializer.serialize(event)
            val outboxMessage = OutboxMessage.init(INVENTORY_DEDUCTION_EVENT, payload)
            outboxMessageRepository.save(outboxMessage)
        } catch (exception: Exception) {
            try {
                inventoryRedisRepository.restoreStocks(event.items)
            } catch (restoreException: Exception) {
                log.error(ErrorMessage.INVENTORY_RESTORE_ERROR.format(event.orderId), restoreException)
            }
            throw exception
        }
    }
}
