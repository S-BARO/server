package com.dh.baro.identity.application

import com.dh.baro.identity.application.dto.AuthResult
import com.dh.baro.identity.domain.*
import com.dh.baro.identity.domain.service.MemberService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthFacade(
    private val clientFactory: OauthClientFactory,
    private val memberService: MemberService,
) {

    @Transactional
    fun login(provider: AuthProvider, accessToken: String): AuthResult {
        val client = clientFactory.getClient(provider)
        val userInfo = client.fetchUser(accessToken)
        val (member, isNew) = memberService.findOrRegister(provider, userInfo)
        return AuthResult(member.id, isNew)
    }
}
