package com.dh.baro.look.presentation.dto

import com.dh.baro.core.LongToStringSerializer
import com.dh.baro.look.domain.Look
import com.fasterxml.jackson.databind.annotation.JsonSerialize

data class LookDto(
    @JsonSerialize(using = LongToStringSerializer::class)
    val lookId: Long,
    val title: String,
    val thumbnailUrl: String,
) {
    companion object {
        fun from(look: Look) = LookDto(
            lookId = look.id,
            title = look.getTitle(),
            thumbnailUrl = look.getThumbnailUrl(),
        )
    }
}
