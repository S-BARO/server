package com.dh.baro.identity.domain

import com.dh.baro.identity.domain.dto.SocialUserInfo

interface OauthClient {
    fun provider(): AuthProvider
    fun fetchUser(accessToken: String): SocialUserInfo
}
