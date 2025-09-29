package com.dh.baro.look.presentation.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class CreateUploadUrlRequest(
    @field:NotBlank(message = "Content-Type은 필수입니다")
    @field:Pattern(
        regexp = "^image/(jpeg|jpg|png|webp)$",
        message = "허용되지 않는 Content-Type입니다. image/jpeg, image/jpg, image/png, image/webp만 허용됩니다"
    )
    val contentType: String,
)
