package com.dh.baro.core.outbox

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class OutboxMessageScheduler(
    private val outboxBatchJob: OutboxBatchJob
) {

    @Scheduled(fixedDelay = 60_000) // 1분
    fun runOutboxJob() {
        outboxBatchJob.run()
    }
}
