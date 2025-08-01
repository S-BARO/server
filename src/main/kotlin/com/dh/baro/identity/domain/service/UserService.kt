package com.dh.baro.identity.domain.service

import com.dh.baro.identity.application.OauthApi
import com.dh.baro.identity.domain.*
import com.dh.baro.identity.domain.dto.RegistrationResult
import com.dh.baro.identity.domain.repository.UserRepository
import com.dh.baro.identity.domain.repository.SocialAccountRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val socialAccountRepository: SocialAccountRepository,
) {

    @Transactional
    fun findOrRegister(
        provider: AuthProvider,
        socialUserInfo: OauthApi.SocialUserInfo
    ): RegistrationResult {
        socialAccountRepository.findByProviderAndProviderId(provider, socialUserInfo.providerId)
            ?.run { return RegistrationResult(user.id, user.role, false) }

        val user = User.newUser(socialUserInfo.nickname, socialUserInfo.email)
            .let { newUser -> userRepository.save(newUser) }
        SocialAccount.of(user, provider, socialUserInfo.providerId)
            .let { socialAccount -> socialAccountRepository.save(socialAccount) }

        return RegistrationResult(
            userId = user.id,
            userRole = user.role,
            isNew = true,
        )
    }
}
