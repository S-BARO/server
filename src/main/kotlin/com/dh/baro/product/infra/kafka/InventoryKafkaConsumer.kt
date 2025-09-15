package com.dh.baro.product.infra.kafka

import com.dh.baro.core.config.KafkaConfig
import com.dh.baro.order.domain.event.OrderPlacedEvent
import com.dh.baro.product.domain.service.InventoryService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class InventoryKafkaConsumer(
    private val inventoryService: InventoryService
) {

    @KafkaListener(
        topics = [KafkaConfig.ORDER_EVENTS_TOPIC],
        groupId = INVENTORY_GROUP_ID,
        containerFactory = "kafkaListenerContainerFactory",
    )
    @Transactional
    fun handleOrderPlacedEvent(
        @Payload event: OrderPlacedEvent,
        @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String,
        @Header(KafkaHeaders.RECEIVED_PARTITION) partition: Int,
        @Header(KafkaHeaders.OFFSET) offset: Long,
        acknowledgment: Acknowledgment
    ) {
        inventoryService.deductStocksFromDatabase(event.items)
        acknowledgment.acknowledge()
    }

    companion object {
        const val INVENTORY_GROUP_ID = "baro-inventory-group"
    }
}
