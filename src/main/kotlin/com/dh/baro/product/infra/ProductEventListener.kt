package com.dh.baro.product.infra

import com.dh.baro.core.exception.InventoryInsufficientException
import com.dh.baro.core.outbox.OutboxMessageRepository
import com.dh.baro.core.outbox.OutboxMessage
import com.dh.baro.core.outbox.OutboxMessageRouter
import com.dh.baro.order.application.event.OrderPlacedEvent
import com.dh.baro.order.infra.IdempotencyService
import com.dh.baro.product.application.event.InventoryInsufficientEvent
import com.dh.baro.product.domain.service.InventoryService
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ProductEventListener(
    private val inventoryService: InventoryService,
    private val outboxMessageRepository: OutboxMessageRepository,
    private val objectMapper: ObjectMapper,
    private val idempotencyService: IdempotencyService,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @KafkaListener(topics = ["order-events"], groupId = "product-service")
    @Transactional
    fun handleOrderPlacedEvent(orderPlacedEvent: OrderPlacedEvent) {
        if (idempotencyService.isAlreadyProcessed(orderPlacedEvent.eventId)) {
            return
        }

        if (!idempotencyService.tryMarkAsProcessing(orderPlacedEvent.eventId)) {
            return
        }

        try {
            inventoryService.deductStocksFromDatabase(orderPlacedEvent.items)
            idempotencyService.markProcessingAsCompleted(orderPlacedEvent.eventId)
        } catch (e: InventoryInsufficientException) {
            log.error("Stock deduction failed for orderId: ${orderPlacedEvent.orderId}", e)

            orderPlacedEvent.items.forEach { item ->
                val inventoryInsufficientEvent = InventoryInsufficientEvent(
                    orderId = orderPlacedEvent.orderId,
                    productId = item.productId,
                    requestedQuantity = item.quantity
                )

                val outboxMessage = OutboxMessage.init(
                    eventType = OutboxMessageRouter.INVENTORY_INSUFFICIENT_EVENT,
                    payload = objectMapper.writeValueAsString(inventoryInsufficientEvent)
                )

                outboxMessageRepository.save(outboxMessage)
            }

            idempotencyService.markProcessingAsCompleted(orderPlacedEvent.eventId, "inventory_insufficient")
        } catch (e: Exception) {
            log.error("Unexpected error during stock deduction for orderId: ${orderPlacedEvent.orderId}", e)
            idempotencyService.removeProcessingState(orderPlacedEvent.eventId)
            throw e
        }
    }
}
