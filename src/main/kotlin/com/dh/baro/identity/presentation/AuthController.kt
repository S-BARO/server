package com.dh.baro.identity.presentation

import com.dh.baro.core.auth.SessionKeys.USER_ID
import com.dh.baro.core.auth.SessionKeys.USER_ROLE
import com.dh.baro.identity.application.AuthFacade
import com.dh.baro.identity.application.dto.LoginResponse
import com.dh.baro.identity.domain.UserRole
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
    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    @PostMapping("/login/oauth")
    @ResponseStatus(HttpStatus.OK)
    override fun loginWithOauth(
        @Valid @RequestBody oauthLoginRequest: OauthLoginRequest,
        request: HttpServletRequest
    ): LoginResponse {
        val result = authFacade.login(oauthLoginRequest.provider, oauthLoginRequest.accessToken)
        logger.info("Login result userId: ${result.userId}")
        request.session.apply {
            setAttribute(USER_ID, result.userId)
            setAttribute(USER_ROLE, result.userRole)
            logger.info("Session saved userId: ${getAttribute(USER_ID)}")
        }
        return LoginResponse(result.isNew)
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun logout(request: HttpServletRequest) {
        request.session.invalidate()
    }
}
