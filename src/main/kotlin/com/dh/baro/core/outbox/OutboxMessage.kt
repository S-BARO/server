package com.dh.baro.core.outbox

import com.dh.baro.core.BaseTimeEntity
import com.dh.baro.core.IdGenerator
import jakarta.persistence.*

@Entity
@Table(
    name = "outbox_messages",
    indexes = [
        Index(name = "idx_outbox_status", columnList = "status"),
    ]
)
class OutboxMessage private constructor(
    @Id
    @Column(name = "id")
    val id: Long,

    @Column(nullable = false, length = 100)
    val eventType: String,

    @Column(columnDefinition = "JSON")
    val payload: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private var status: MessageStatus,

    @Column(nullable = false)
    private var tryCount: Int = 0,

    ) : BaseTimeEntity() {

    override fun getId(): Long = id

    fun markSendSuccess() {
        status = MessageStatus.SEND_SUCCESS
    }

    fun isDeadMessage(): Boolean {
        return status == MessageStatus.DEAD
    }

    fun markSendFail() {
        tryCount++

        if (tryCount >= MAX_TRY_COUNT) {
            status = MessageStatus.DEAD
        } else {
            status = MessageStatus.SEND_FAIL
        }
    }

    companion object {
        private const val MAX_TRY_COUNT = 3

        fun init(eventType: String, payload: String): OutboxMessage {
            return OutboxMessage(
                id = IdGenerator.generate(),
                eventType = eventType,
                payload = payload,
                status = MessageStatus.INIT
            )
        }
    }
}
