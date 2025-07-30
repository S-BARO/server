package com.dh.baro.identity.application

import com.dh.baro.identity.domain.AuthProvider

interface OauthApiFactory {
    fun getClient(provider: AuthProvider): OauthApi
}
