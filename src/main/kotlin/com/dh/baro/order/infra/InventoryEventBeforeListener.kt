package com.dh.baro.order.infra

import com.dh.baro.core.ErrorMessage
import com.dh.baro.product.infra.event.InventoryDeductionRequestedEvent
import com.dh.baro.product.infra.redis.InventoryRedisRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class InventoryEventBeforeListener(
    private val inventoryRedisRepository: InventoryRedisRepository,
) {

    private val log = LoggerFactory.getLogger(javaClass)

//    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
//    fun restoreInventoryOnRollback(event: InventoryDeductionRequestedEvent) {
//        runCatching {
//            inventoryRedisRepository.restoreStocks(event.items)
//        }.onFailure { ex ->
//            log.error(ErrorMessage.INVENTORY_RESTORE_ERROR.format(event.orderId), ex)
//        }
//    }
}
