package com.dh.baro.identity.application

import com.dh.baro.identity.application.dto.AuthResult
import com.dh.baro.identity.domain.*
import com.dh.baro.identity.domain.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AuthFacade(
    private val oauthApiFactory: OauthApiFactory,
    private val userService: UserService,
) {

    private val logger = LoggerFactory.getLogger(AuthFacade::class.java)

    fun login(provider: AuthProvider, accessToken: String): AuthResult {
        logger.info("AuthFacade.login 시작 - provider: {}", provider)

        try {
            // OAuth API 클라이언트 획득
            val oauthApi = oauthApiFactory.getClient(provider)
            logger.debug("OAuth API 클라이언트 획득 완료 - provider: {}, apiType: {}",
                provider, oauthApi::class.simpleName)

            // 소셜 플랫폼에서 사용자 정보 조회
            val socialUserInfo = oauthApi.fetchUser(accessToken)
            logger.info("소셜 사용자 정보 조회 완료 - provider: {}, providerId: {}, nickname: {}, email: {}",
                provider, socialUserInfo.providerId, socialUserInfo.nickname, socialUserInfo.email)

            // 사용자 찾기 또는 등록
            val response = userService.findOrRegister(provider, socialUserInfo)
            logger.info("UserService.findOrRegister 완료 - userId: {}, userRole: {}, isNew: {}",
                response.userId, response.userRole, response.isNew)

            val authResult = AuthResult(response.userId, response.userRole, response.isNew)
            logger.info("AuthFacade.login 성공 - provider: {}, userId: {}, isNew: {}",
                provider, authResult.userId, authResult.isNew)

            return authResult

        } catch (e: Exception) {
            logger.error("AuthFacade.login 실패 - provider: {}, error: {}",
                provider, e.message, e)
            throw e
        }
    }
}
