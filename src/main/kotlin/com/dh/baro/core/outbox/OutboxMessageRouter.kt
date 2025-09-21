package com.dh.baro.core.outbox

import com.dh.baro.core.ErrorMessage
import com.dh.baro.core.config.KafkaConfig
import com.dh.baro.order.application.event.OrderPlacedEvent
import com.dh.baro.product.application.event.InventoryInsufficientEvent
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class OutboxMessageRouter(
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    private val objectMapper: ObjectMapper,
) {

    fun route(msg: OutboxMessage): Boolean {
        return try {
            when (msg.eventType) {
                ORDER_PLACED_EVENT -> {
                    val event = objectMapper.readValue(msg.payload, OrderPlacedEvent::class.java)
                    val future = kafkaTemplate.send(KafkaConfig.ORDER_EVENTS_TOPIC, event.orderId.toString(), event)
                    future.get(1, TimeUnit.SECONDS)
                    true
                }

                INVENTORY_INSUFFICIENT_EVENT -> {
                    val event = objectMapper.readValue(msg.payload, InventoryInsufficientEvent::class.java)
                    val future = kafkaTemplate.send(KafkaConfig.INVENTORY_EVENTS_TOPIC, event.orderId.toString(), event)
                    future.get(1, TimeUnit.SECONDS)
                    true
                }

                else -> throw IllegalStateException(ErrorMessage.UNKNOWN_EVENT_TYPE.format(msg.eventType))
            }
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        const val ORDER_PLACED_EVENT = "ORDER_PLACED"
        const val INVENTORY_INSUFFICIENT_EVENT = "INVENTORY_INSUFFICIENT"
    }
}
