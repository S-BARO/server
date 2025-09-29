package com.dh.baro.identity.domain.service

import com.dh.baro.core.ErrorMessage
import com.dh.baro.identity.application.OauthApi
import com.dh.baro.identity.domain.*
import com.dh.baro.identity.domain.dto.RegistrationResult
import com.dh.baro.identity.domain.repository.UserRepository
import com.dh.baro.identity.domain.repository.SocialAccountRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val socialAccountRepository: SocialAccountRepository,
) {

    fun checkUserExists(userId: Long) {
        require(userRepository.existsById(userId)) {
            ErrorMessage.USER_NOT_FOUND.format(userId)
        }
    }

    fun getUserById(userId: Long): User =
        userRepository.findByIdOrNull(userId)
            ?: throw IllegalArgumentException(ErrorMessage.USER_NOT_FOUND.format(userId))

    @Transactional
    fun findOrRegister(
        provider: AuthProvider,
        socialUserInfo: OauthApi.SocialUserInfo
    ): RegistrationResult {
        val existingSocialAccount = socialAccountRepository.findByProviderAndProviderId(provider, socialUserInfo.providerId)
        if(existingSocialAccount != null) {
            return RegistrationResult(existingSocialAccount.user.id, existingSocialAccount.user.role, false)
        }

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
