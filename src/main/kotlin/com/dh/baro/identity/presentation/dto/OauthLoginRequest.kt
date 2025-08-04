package com.dh.baro.identity.presentation.dto

import com.dh.baro.identity.domain.AuthProvider
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class OauthLoginRequest (
    @field:NotNull(message = "provider 값은 필수입니다.")
    val provider: AuthProvider,

    @field:NotBlank(message = "accessToken 값은 비어있을 수 없습니다.")
    val accessToken: String,
)
