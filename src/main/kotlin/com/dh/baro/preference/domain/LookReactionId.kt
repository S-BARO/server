package com.dh.baro.preference.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class LookReactionId(
    @Column(name = "user_id")
    val userId: Long,

    @Column(name = "look_id")
    val lookId: Long
) : Serializable
