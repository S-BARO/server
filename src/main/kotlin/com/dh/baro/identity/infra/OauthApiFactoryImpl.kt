package com.dh.baro.identity.infra

import com.dh.baro.core.ErrorMessage
import com.dh.baro.identity.domain.AuthProvider
import com.dh.baro.identity.application.OauthApi
import com.dh.baro.identity.application.OauthApiFactory
import org.springframework.stereotype.Component

@Component
class OauthApiFactoryImpl(
    oauthApis: List<OauthApi>
) : OauthApiFactory {

    private val map = oauthApis.associateBy { it.provider() }

    override fun getClient(provider: AuthProvider): OauthApi =
        map[provider] ?: throw IllegalArgumentException(ErrorMessage.UNSUPPORTED_SOCIAL_LOGIN.format(provider))
}
