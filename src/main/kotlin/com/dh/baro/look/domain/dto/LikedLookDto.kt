package com.dh.baro.look.domain.dto

import com.dh.baro.core.serialization.LongToStringSerializer
import com.fasterxml.jackson.databind.annotation.JsonSerialize

data class LikedLookDto(
    @JsonSerialize(using = LongToStringSerializer::class)
    val lookReactionId: Long,
    @JsonSerialize(using = LongToStringSerializer::class)
    val lookId: Long,
    val title: String,
    val thumbnailUrl: String,
)
