package com.dh.baro.identity.application

import com.dh.baro.identity.application.dto.AuthResult
import com.dh.baro.identity.domain.*
import com.dh.baro.identity.domain.service.UserService
import org.springframework.stereotype.Service

@Service
class AuthFacade(
    private val oauthApiFactory: OauthApiFactory,
    private val userService: UserService,
) {

    fun login(provider: AuthProvider, accessToken: String): AuthResult {
        val oauthApi = oauthApiFactory.getClient(provider)
        val socialUserInfo = oauthApi.fetchUser(accessToken)
        val response = userService.findOrRegister(provider, socialUserInfo)
        return AuthResult(response.userId, response.userRole, response.isNew)
    }
}
