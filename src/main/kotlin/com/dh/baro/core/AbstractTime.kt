package com.dh.baro.core

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PrePersist
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class AbstractTime(
    @CreatedDate
    @Column(name = "created_at", columnDefinition = "TIMESTAMP", nullable = false, updatable = false)
    var createdAt: Instant = instant(),

    @LastModifiedDate
    @Column(name = "modified_at", columnDefinition = "TIMESTAMP")
    var modifiedAt: Instant? = null,
) {
    @PrePersist
    fun prePersist() {
        if (modifiedAt == null) {
            modifiedAt = createdAt
        }
    }
}
