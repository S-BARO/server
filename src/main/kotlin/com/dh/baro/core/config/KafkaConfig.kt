package com.dh.baro.core.config

import com.dh.baro.order.application.event.OrderPlacedEvent
import com.dh.baro.product.application.event.InventoryDeductionCompletedEvent
import com.dh.baro.product.application.event.InventoryInsufficientEvent
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.*
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer
import org.springframework.util.backoff.FixedBackOff
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer

@Configuration
class KafkaConfig {

    @Value("\${spring.kafka.bootstrap-servers}")
    private lateinit var bootstrapServers: String

    @Bean
    fun producerFactory(): ProducerFactory<String, Any> {
        val configProps = buildProducerProperties()
        val producerFactory = DefaultKafkaProducerFactory<String, Any>(configProps)

        return producerFactory
    }

    private fun buildProducerProperties(): Map<String, Any> {
        return mapOf(
            // 연결 설정
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,

            // 직렬화 설정
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java,

            // 안전성 설정 (Exactly-Once Semantics)
            ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG to true,
            ProducerConfig.ACKS_CONFIG to "all",
            ProducerConfig.RETRIES_CONFIG to Int.MAX_VALUE,
            ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION to 1,

            // 성능 설정
            ProducerConfig.BATCH_SIZE_CONFIG to 16384,
            ProducerConfig.LINGER_MS_CONFIG to 10,
            ProducerConfig.BUFFER_MEMORY_CONFIG to 33554432,

            // 타입 매핑 설정
            JsonSerializer.TYPE_MAPPINGS to buildTypeMapping()
        )
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, Any> {
        val template = KafkaTemplate(producerFactory())
        template.defaultTopic = "default-topic"
        return template
    }

    @Bean
    fun consumerFactory(): ConsumerFactory<String, Any> {
        val configProps = buildConsumerProperties()

        return DefaultKafkaConsumerFactory(configProps)
    }

    private fun buildConsumerProperties(): Map<String, Any> {
        return mapOf(
            // 연결 설정
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,

            // 역직렬화 설정 - ErrorHandlingDeserializer로 감싸기
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to ErrorHandlingDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to ErrorHandlingDeserializer::class.java,
            ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS to StringDeserializer::class.java,
            ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS to JsonDeserializer::class.java,

            // 안전성 설정
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to false,
            ConsumerConfig.ISOLATION_LEVEL_CONFIG to "read_committed",

            // 성능 설정
            ConsumerConfig.MAX_POLL_RECORDS_CONFIG to 100,
            ConsumerConfig.FETCH_MIN_BYTES_CONFIG to 1024,
            ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG to 500,

            // JSON 역직렬화 설정
            JsonDeserializer.TRUSTED_PACKAGES to "com.dh.baro",
            JsonDeserializer.TYPE_MAPPINGS to buildTypeMapping(),
            JsonDeserializer.USE_TYPE_INFO_HEADERS to true
        )
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Any> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, Any>()
        factory.consumerFactory = consumerFactory()
        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL
        factory.setConcurrency(3) // 동시 처리 스레드 수

        // DLQ 처리
        val recoverer = DeadLetterPublishingRecoverer(kafkaTemplate()) { record, _ ->
            val dlqTopic = when (record.topic()) {
                ORDER_EVENTS_TOPIC -> ORDER_EVENTS_DLQ_TOPIC
                INVENTORY_EVENTS_TOPIC -> INVENTORY_EVENTS_DLQ_TOPIC
                else -> "${record.topic()}-dlq"
            }
            TopicPartition(dlqTopic, record.partition())
        }

        val errorHandler = DefaultErrorHandler(recoverer, FixedBackOff(1000L, 3L))
        factory.setCommonErrorHandler(errorHandler)

        return factory
    }

    private fun buildTypeMapping(): String {
        val mappings = mapOf(
            "OrderPlacedEvent" to OrderPlacedEvent::class.java.name,
            "InventoryDeductionCompletedEvent" to InventoryDeductionCompletedEvent::class.java.name,
            "InventoryInsufficientEvent" to InventoryInsufficientEvent::class.java.name
        )

        return mappings.entries.joinToString(",") { "${it.key}:${it.value}" }
    }

    companion object {
        const val ORDER_EVENTS_TOPIC = "order-events"
        const val ORDER_EVENTS_DLQ_TOPIC = "order-events-dlq"
        const val INVENTORY_EVENTS_TOPIC = "inventory-events"
        const val INVENTORY_EVENTS_DLQ_TOPIC = "inventory-events-dlq"
    }
}
