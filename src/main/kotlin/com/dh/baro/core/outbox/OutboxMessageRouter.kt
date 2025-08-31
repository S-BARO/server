package com.dh.baro.core.outbox

import com.dh.baro.core.ErrorMessage
import com.dh.baro.product.infra.event.InventoryDeductionRequestedEvent
import com.dh.baro.product.infra.event.InventoryEventHandler
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component

@Component
class OutboxMessageRouter(
    private val inventoryEventHandler: InventoryEventHandler,
    private val objectMapper: ObjectMapper,
) {

    fun route(msg: OutboxMessage) {
        when (msg.eventType) {
            INVENTORY_DEDUCTION_EVENT -> {
                val event = objectMapper.readValue(msg.payload, InventoryDeductionRequestedEvent::class.java)
                inventoryEventHandler.handleEvent(event)
            }

            else -> throw IllegalStateException(ErrorMessage.UNKNOWN_EVENT_TYPE.format(msg.eventType))
        }
    }

    companion object {
        const val INVENTORY_DEDUCTION_EVENT = "INVENTORY_DEDUCTION_REQUESTED"
    }
}
