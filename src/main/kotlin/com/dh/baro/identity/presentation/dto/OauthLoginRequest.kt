package com.dh.baro.identity.presentation.dto

import com.dh.baro.identity.domain.AuthProvider

data class OauthLoginRequest (
    val provider: AuthProvider,
    val accessToken: String,
)
