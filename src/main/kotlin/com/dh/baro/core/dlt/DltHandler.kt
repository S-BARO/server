package com.dh.baro.core.dlt

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.DltHandler
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class DltHandler(
    private val failedMessageRepository: FailedMessageRepository,
    private val objectMapper: ObjectMapper,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @DltHandler
    @Transactional
    fun handleDltMessage(
        consumerRecord: ConsumerRecord<String, Any>,
        @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String,
        @Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) exceptionMessage: String?,
        @Header(value = "kafka_dlt-original-consumer-group", required = false) consumerGroup: String?
    ) {
        try {
            val eventId = extractEventId(consumerRecord.value())
            val payload = objectMapper.writeValueAsString(consumerRecord.value())
            val errorMessage = exceptionMessage ?: "Unknown error"
            val group = consumerGroup ?: "unknown-group"

            val failedMessage = FailedMessage.create(
                topic = topic,
                eventId = eventId,
                payload = payload,
                errorMessage = errorMessage,
                retryCount = 3,
                consumerGroup = group
            )

            failedMessageRepository.save(failedMessage)
            log.error("DLT 메시지 저장 완료: topic=$topic, eventId=$eventId")

        } catch (e: Exception) {
            log.error("DLT 메시지 처리 실패: topic=$topic", e)
        }
    }

    private fun extractEventId(message: Any): Long {
        return try {
            when (message) {
                is Map<*, *> -> {
                    (message["eventId"] as? Number)?.toLong() ?: 0L
                }
                else -> {
                    val jsonNode = objectMapper.valueToTree<com.fasterxml.jackson.databind.JsonNode>(message)
                    jsonNode.get("eventId")?.asLong() ?: 0L
                }
            }
        } catch (e: Exception) {
            log.warn("eventId 추출 실패, 기본값 사용: $e")
            0L
        }
    }
}
