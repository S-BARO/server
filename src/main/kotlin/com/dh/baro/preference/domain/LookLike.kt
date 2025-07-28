package com.dh.baro.preference.domain

import com.dh.baro.core.AbstractTime
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "look_likes")
class LookLike(
    @EmbeddedId
    val id: LookReactionId
) : AbstractTime()
