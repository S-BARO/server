package com.dh.baro.product.infra.event

import com.dh.baro.core.config.AsyncConfig.Companion.EVENT_ASYNC_TASK_EXECUTOR
import com.dh.baro.order.domain.OrderService
import com.dh.baro.product.domain.service.InventoryService
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class InventoryEventAfterListener(
    private val orderService: OrderService,
    private val inventoryService: InventoryService,
) {

    @Async(EVENT_ASYNC_TASK_EXECUTOR)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleInventoryDeductionEvent(event: InventoryDeductionRequestedEvent) {
        event.items.forEach { item ->
            inventoryService.deductStockFromDB(item.productId, item.quantity)
        }
        orderService.confirmOrder(event.orderId)
    }
}
