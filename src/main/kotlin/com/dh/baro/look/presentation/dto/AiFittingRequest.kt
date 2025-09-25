package com.dh.baro.look.presentation.dto

import jakarta.validation.constraints.NotBlank

data class AiFittingRequest(
    @field:NotBlank
    val sourceImageUrl: String,

    @field:NotBlank
    val clothingImageUrl: String,
)
