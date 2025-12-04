package com.dh.baro.look.domain.dto

data class LikedLookDto(
    val lookReactionId: Long,
    val lookId: Long,
    val title: String,
    val thumbnailUrl: String,
)
