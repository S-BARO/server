package com.dh.baro.identity.domain.service

import com.dh.baro.core.ErrorMessage
import com.dh.baro.identity.application.OauthApi
import com.dh.baro.identity.domain.*
import com.dh.baro.identity.domain.dto.RegistrationResult
import com.dh.baro.identity.domain.repository.UserRepository
import com.dh.baro.identity.domain.repository.SocialAccountRepository
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val socialAccountRepository: SocialAccountRepository,
) {

    private val logger = LoggerFactory.getLogger(UserService::class.java)

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
        logger.info("UserService.findOrRegister 시작 - provider: {}, providerId: {}, nickname: {}, email: {}",
            provider, socialUserInfo.providerId, socialUserInfo.nickname, socialUserInfo.email)

        try {
            // 기존 소셜 계정 조회
            val existingSocialAccount = socialAccountRepository.findByProviderAndProviderId(provider, socialUserInfo.providerId)
            logger.debug("기존 소셜 계정 조회 완료 - provider: {}, providerId: {}, found: {}",
                provider, socialUserInfo.providerId, existingSocialAccount != null)

            if (existingSocialAccount != null) {
                val userId = existingSocialAccount.user.id
                val userRole = existingSocialAccount.user.role
                logger.info("기존 사용자 로그인 - userId: {}, userRole: {}, provider: {}, providerId: {}",
                    userId, userRole, provider, socialUserInfo.providerId)

                // userId가 0인 경우 경고 로그
                if (userId == 0L) {
                    logger.error("기존 사용자의 ID가 0입니다! - provider: {}, providerId: {}, user: {}",
                        provider, socialUserInfo.providerId, existingSocialAccount.user)
                }

                return RegistrationResult(userId, userRole, false)
            }

            // 새 사용자 생성
            logger.info("새 사용자 생성 시작 - provider: {}, providerId: {}, nickname: {}, email: {}",
                provider, socialUserInfo.providerId, socialUserInfo.nickname, socialUserInfo.email)

            val user = User.newUser(socialUserInfo.nickname, socialUserInfo.email)
            logger.debug("User.newUser 생성 완료 - generatedId: {}, nickname: {}, email: {}",
                user.id, user.getName(), user.getEmail())

            // userId가 0인 경우 경고 로그
            if (user.id == 0L) {
                logger.error("새로 생성된 사용자의 ID가 0입니다! - nickname: {}, email: {}, user: {}",
                    socialUserInfo.nickname, socialUserInfo.email, user)
            }

            val savedUser = userRepository.save(user)
            logger.info("사용자 저장 완료 - savedUserId: {}, originalId: {}, nickname: {}, email: {}",
                savedUser.id, user.id, savedUser.getName(), savedUser.getEmail())

            // 저장 후에도 ID 확인
            if (savedUser.id == 0L) {
                logger.error("저장된 사용자의 ID가 0입니다! - nickname: {}, email: {}, savedUser: {}",
                    socialUserInfo.nickname, socialUserInfo.email, savedUser)
            }

            val socialAccount = SocialAccount.of(savedUser, provider, socialUserInfo.providerId)
            val savedSocialAccount = socialAccountRepository.save(socialAccount)
            logger.debug("소셜 계정 저장 완료 - userId: {}, provider: {}, providerId: {}",
                savedUser.id, provider, socialUserInfo.providerId)

            val result = RegistrationResult(
                userId = savedUser.id,
                userRole = savedUser.role,
                isNew = true,
            )

            logger.info("새 사용자 등록 완료 - userId: {}, userRole: {}, provider: {}, providerId: {}",
                result.userId, result.userRole, provider, socialUserInfo.providerId)

            return result

        } catch (e: Exception) {
            logger.error("UserService.findOrRegister 실패 - provider: {}, providerId: {}, nickname: {}, error: {}",
                provider, socialUserInfo.providerId, socialUserInfo.nickname, e.message, e)
            throw e
        }
    }
}
