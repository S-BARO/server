package com.dh.baro.core

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.domain.Persistable
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseTimeEntity(
    @CreatedDate
    @Column(name = "created_at", columnDefinition = "TIMESTAMP", nullable = false, updatable = false)
    var createdAt: Instant? = null,

    @LastModifiedDate
    @Column(name = "modified_at", columnDefinition = "TIMESTAMP")
    var modifiedAt: Instant? = null,
) : Persistable<Long> {

    override fun isNew(): Boolean = (this.createdAt == null)

    @PrePersist
    fun prePersist() {
        if (createdAt == null) {
            createdAt = instant()
        }

        if (modifiedAt == null) {
            modifiedAt = createdAt
        }
    }
}
