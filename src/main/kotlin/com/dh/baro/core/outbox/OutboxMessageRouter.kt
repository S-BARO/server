package com.dh.baro.core.outbox

import com.dh.baro.core.ErrorMessage
import com.dh.baro.core.config.KafkaConfig
import com.dh.baro.order.domain.event.OrderPlacedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class OutboxMessageRouter(
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    private val objectMapper: ObjectMapper,
) {

    fun route(msg: OutboxMessage) {
        when (msg.eventType) {
            ORDER_PLACED_EVENT -> {
                val event = objectMapper.readValue(msg.payload, OrderPlacedEvent::class.java)
                kafkaTemplate.send(KafkaConfig.ORDER_EVENTS_TOPIC, event.orderId.toString(), event)
            }

            else -> throw IllegalStateException(ErrorMessage.UNKNOWN_EVENT_TYPE.format(msg.eventType))
        }
    }

    companion object {
        const val ORDER_PLACED_EVENT = "ORDER_PLACED"
    }
}
