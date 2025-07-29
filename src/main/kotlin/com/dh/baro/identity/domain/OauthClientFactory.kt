package com.dh.baro.identity.domain

interface OauthClientFactory {
    fun getClient(provider: AuthProvider): OauthClient
}
