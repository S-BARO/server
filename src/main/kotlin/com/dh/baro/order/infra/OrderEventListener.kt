package com.dh.baro.order.infra

import com.dh.baro.core.ErrorMessage
import com.dh.baro.core.outbox.OutboxMessage
import com.dh.baro.core.outbox.OutboxMessageRepository
import com.dh.baro.core.outbox.OutboxMessageRouter
import com.dh.baro.order.application.event.OrderPlacedEvent
import com.dh.baro.order.domain.OrderRepository
import com.dh.baro.order.domain.OrderStatus
import org.springframework.data.repository.findByIdOrNull
import com.dh.baro.product.application.event.InventoryInsufficientEvent
import com.dh.baro.product.domain.InventoryItem
import com.dh.baro.product.domain.service.InventoryService
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class OrderEventListener(
    private val outboxMessageRepository: OutboxMessageRepository,
    private val objectMapper: ObjectMapper,
    private val orderRepository: OrderRepository,
    private val inventoryService: InventoryService,
    private val idempotencyService: IdempotencyService,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun saveToOutbox(event: OrderPlacedEvent) {
        val payload = objectMapper.writeValueAsString(event)
        val outboxMessage = OutboxMessage.init(
            eventType = OutboxMessageRouter.ORDER_PLACED_EVENT,
            payload = payload
        )
        outboxMessageRepository.save(outboxMessage)
    }

    @KafkaListener(topics = ["inventory-events"], groupId = "order-service")
    @Transactional
    fun handleInventoryInsufficientEvent(inventoryInsufficientEvent: InventoryInsufficientEvent) {
        if (idempotencyService.isAlreadyProcessed(inventoryInsufficientEvent.eventId)) {
            return
        }

        if (!idempotencyService.tryMarkAsProcessing(inventoryInsufficientEvent.eventId)) {
            return
        }

        try {
            val order = orderRepository.findByIdOrNull(inventoryInsufficientEvent.orderId)
                ?: throw IllegalArgumentException(ErrorMessage.ORDER_NOT_FOUND.format(inventoryInsufficientEvent.orderId))

            order.changeStatus(OrderStatus.CANCELLED)
            orderRepository.save(order)

            val inventoryItems = order.items.map {
                InventoryItem(
                    productId = it.productId,
                    quantity = it.quantity
                )
            }

            val rollbackSuccess = inventoryService.rollbackStocks(inventoryItems)
            if (!rollbackSuccess) {
                val errorMessage = ErrorMessage.INVENTORY_RESTORE_ERROR.format(inventoryInsufficientEvent.orderId)
                log.error(errorMessage)
                throw RuntimeException(errorMessage)
            }

            idempotencyService.markProcessingAsCompleted(inventoryInsufficientEvent.eventId)
        } catch (e: Exception) {
            idempotencyService.removeProcessingState(inventoryInsufficientEvent.eventId)
            throw e
        }
    }

}
