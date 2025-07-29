package com.dh.baro.identity.infra

import com.dh.baro.identity.domain.AuthProvider
import com.dh.baro.identity.domain.OauthClient
import com.dh.baro.identity.domain.OauthClientFactory
import org.springframework.stereotype.Component

@Component
class OauthClientFactoryImpl(
    oauthClients: List<OauthClient>
) : OauthClientFactory {
    private val map = oauthClients.associateBy { it.provider() }

    override fun getClient(provider: AuthProvider) =
        map[provider] ?: throw UnsupportedException("UNSUPPORTED_SOCIAL_LOGIN", provider.name)
}
