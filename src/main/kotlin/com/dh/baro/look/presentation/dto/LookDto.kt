package com.dh.baro.look.presentation.dto

import com.dh.baro.look.domain.Look

data class LookDto(
    val lookId: String,
    val title: String,
    val thumbnailUrl: String,
) {
    companion object {
        fun from(look: Look) = LookDto(
            lookId = look.id.toString(),
            title = look.getTitle(),
            thumbnailUrl = look.getThumbnailUrl(),
        )
    }
}
