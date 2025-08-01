package com.dh.baro.preference.domain

import com.dh.baro.core.AbstractTime
import com.dh.baro.identity.domain.User
import com.dh.baro.look.domain.Look
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table

@Entity
@Table(name = "look_likes")
class LookLike(
    @EmbeddedId
    val id: LookReactionId,

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    val user: User,

    @MapsId("lookId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "look_id", insertable = false, updatable = false)
    val look: Look
) : AbstractTime()
