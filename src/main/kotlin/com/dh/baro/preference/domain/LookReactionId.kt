package com.dh.baro.preference.domain

import com.dh.baro.identity.domain.Member
import com.dh.baro.look.domain.Look
import jakarta.persistence.Embeddable
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.io.Serializable

@Embeddable
data class LookReactionId(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "look_id")
    val look: Look
) : Serializable
