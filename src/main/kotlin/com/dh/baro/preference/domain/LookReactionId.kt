package com.dh.baro.preference.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class LookReactionId(
    @Column(name = "member_id")
    val memberId: Long,

    @Column(name = "look_id")
    val lookId: Long
) : Serializable
