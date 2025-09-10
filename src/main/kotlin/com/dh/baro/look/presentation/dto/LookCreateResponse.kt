package com.dh.baro.look.presentation.dto

import com.dh.baro.core.LongToStringSerializer
import com.dh.baro.look.domain.Look
import com.fasterxml.jackson.databind.annotation.JsonSerialize

data class LookCreateResponse(
    @JsonSerialize(using = LongToStringSerializer::class)
    val lookId: Long,
) {

    companion object {
        fun from(look: Look): LookCreateResponse {
            return LookCreateResponse(
                lookId = look.id,
            )
        }
    }
}
