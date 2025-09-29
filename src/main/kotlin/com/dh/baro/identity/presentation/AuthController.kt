package com.dh.baro.identity.presentation

import com.dh.baro.core.auth.SessionKeys.USER_ID
import com.dh.baro.core.auth.SessionKeys.USER_ROLE
import com.dh.baro.identity.application.AuthFacade
import com.dh.baro.identity.application.dto.LoginResponse
import com.dh.baro.identity.presentation.dto.OauthLoginRequest
import com.dh.baro.identity.presentation.swagger.AuthSwagger
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authFacade: AuthFacade
) : AuthSwagger {

    private val logger = LoggerFactory.getLogger(AuthController::class.java)

    @PostMapping("/login/oauth")
    @ResponseStatus(HttpStatus.OK)
    override fun loginWithOauth(
        @Valid @RequestBody oauthLoginRequest: OauthLoginRequest,
        request: HttpServletRequest
    ): LoginResponse {
        val sessionId = request.session.id
        logger.info("OAuth 로그인 요청 시작 - provider: {}, sessionId: {}", oauthLoginRequest.provider, sessionId)

        try {
            val result = authFacade.login(oauthLoginRequest.provider, oauthLoginRequest.accessToken)
            logger.info("AuthFacade.login 완료 - userId: {}, userRole: {}, isNew: {}, sessionId: {}",
                result.userId, result.userRole, result.isNew, sessionId)

            // 세션에 사용자 정보 저장 전 확인
            logger.debug("세션 속성 설정 전 - userId: {}, userRole: {}, sessionId: {}",
                result.userId, result.userRole, sessionId)

            request.session.apply {
                setAttribute(USER_ID, result.userId)
                setAttribute(USER_ROLE, result.userRole)
                logger.debug("세션 속성 설정 완료 - USER_ID: {}, USER_ROLE: {}, sessionId: {}",
                    getAttribute(USER_ID), getAttribute(USER_ROLE), sessionId)
            }

            logger.info("OAuth 로그인 성공 - userId: {}, isNew: {}, sessionId: {}",
                result.userId, result.isNew, sessionId)
            return LoginResponse(result.isNew)

        } catch (e: Exception) {
            logger.error("OAuth 로그인 실패 - provider: {}, sessionId: {}, error: {}",
                oauthLoginRequest.provider, sessionId, e.message, e)
            throw e
        }
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun logout(request: HttpServletRequest) {
        request.session.invalidate()
    }
}
