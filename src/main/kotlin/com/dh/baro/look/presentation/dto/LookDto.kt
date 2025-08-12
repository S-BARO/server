package com.dh.baro.look.presentation.dto

import com.dh.baro.look.domain.Look

data class LookDto(
    val id: Long,
    val title: String,
    val thumbnailUrl: String,
) {
    companion object {
        fun from(look: Look) = LookDto(
            id = look.id,
            title = look.getTitle(),
            thumbnailUrl = look.getThumbnailUrl(),
        )
    }
}
