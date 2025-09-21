package com.dh.baro.core.dlq

import com.dh.baro.core.BaseTimeEntity
import com.dh.baro.core.IdGenerator
import jakarta.persistence.*

@Entity
@Table(name = "failed_messages")
class FailedMessage private constructor(
    @Id
    @Column(name = "id")
    val id: Long,

    @Column(name = "topic", nullable = false, length = 100)
    val topic: String,

    @Column(name = "event_id", nullable = false)
    val eventId: Long,

    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    val payload: String,

    @Column(name = "error_message", nullable = false, columnDefinition = "TEXT")
    val errorMessage: String,

    @Column(name = "retry_count", nullable = false)
    val retryCount: Int,

    @Column(name = "consumer_group", nullable = false, length = 50)
    val consumerGroup: String,
) : BaseTimeEntity() {

    override fun getId(): Long = id

    companion object {
        fun create(
            topic: String,
            eventId: Long,
            payload: String,
            errorMessage: String,
            retryCount: Int,
            consumerGroup: String,
        ): FailedMessage {
            return FailedMessage(
                id = IdGenerator.generate(),
                topic = topic,
                eventId = eventId,
                payload = payload,
                errorMessage = errorMessage,
                retryCount = retryCount,
                consumerGroup = consumerGroup,
            )
        }
    }
}
