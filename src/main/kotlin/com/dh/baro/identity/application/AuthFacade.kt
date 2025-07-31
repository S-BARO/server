package com.dh.baro.identity.application

import com.dh.baro.identity.application.dto.AuthResult
import com.dh.baro.identity.domain.*
import com.dh.baro.identity.domain.service.MemberService
import org.springframework.stereotype.Service

@Service
class AuthFacade(
    private val oauthApiFactory: OauthApiFactory,
    private val memberService: MemberService,
) {

    fun login(provider: AuthProvider, accessToken: String): AuthResult {
        val oauthApi = oauthApiFactory.getClient(provider)
        val socialUserInfo = oauthApi.fetchUser(accessToken)
        val response = memberService.findOrRegister(provider, socialUserInfo)
        return AuthResult(response.memberId, response.memberRole, response.isNew)
    }
}
