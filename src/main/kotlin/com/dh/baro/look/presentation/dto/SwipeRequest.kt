package com.dh.baro.look.presentation.dto

import com.dh.baro.look.domain.ReactionType
import jakarta.validation.constraints.NotNull

data class SwipeRequest(
    @field:NotNull(message = "reactionType 값은 필수입니다.")
    val reactionType: ReactionType,
)
