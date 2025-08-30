package com.dh.baro.product.infra.event

import com.dh.baro.core.config.AsyncConfig.Companion.EVENT_ASYNC_TASK_EXECUTOR
import com.dh.baro.core.outbox.OutboxBatchJob
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class InventoryEventListener(
    private val outboxBatchJob: OutboxBatchJob,
) {

    @Async(EVENT_ASYNC_TASK_EXECUTOR)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun processInventoryDeduction(event: InventoryDeductionRequestedEvent) {
        outboxBatchJob.run()
    }
}
