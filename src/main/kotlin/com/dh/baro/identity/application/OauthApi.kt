package com.dh.baro.identity.application

import com.dh.baro.identity.domain.AuthProvider

interface OauthApi {
    fun provider(): AuthProvider
    fun fetchUser(accessToken: String): SocialUserInfo

    class SocialUserInfo(
        val providerId: String,
        val email: String,
        val nickname: String,
    )
}
