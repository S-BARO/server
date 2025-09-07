package com.dh.baro.look.presentation.dto

import com.dh.baro.look.domain.Look

data class LookCreateResponse(
    val lookId: String,
) {

    companion object {
        fun from(look: Look): LookCreateResponse {
            return LookCreateResponse(
                lookId = look.id.toString(),
            )
        }
    }
}
