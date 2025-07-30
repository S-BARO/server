package com.dh.baro.identity.domain.service

import com.dh.baro.identity.application.OauthApi
import com.dh.baro.identity.domain.*
import com.dh.baro.identity.domain.dto.RegistrationResult
import com.dh.baro.identity.domain.repository.MemberRepository
import com.dh.baro.identity.domain.repository.SocialAccountRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val socialAccountRepository: SocialAccountRepository,
) {

    @Transactional
    fun findOrRegister(
        provider: AuthProvider,
        socialUserInfo: OauthApi.SocialUserInfo
    ): RegistrationResult {
        socialAccountRepository.findByProviderAndProviderId(provider, socialUserInfo.providerId).orElse(null)
            ?.let { existingAccount ->
                return RegistrationResult(existingAccount.member, isNew = false)
            }

        val member = Member.newMember(socialUserInfo.nickname, socialUserInfo.email)
            .let { newMember -> memberRepository.save(newMember) }
        SocialAccount.of(member, provider, socialUserInfo.providerId)
            .let { socialAccount -> socialAccountRepository.save(socialAccount) }

        return RegistrationResult(member, isNew = true)
    }
}
