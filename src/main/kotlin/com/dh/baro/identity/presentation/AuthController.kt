package com.dh.baro.identity.presentation

import com.dh.baro.core.auth.SessionKeys.USER_ID
import com.dh.baro.core.auth.SessionKeys.USER_ROLE
import com.dh.baro.identity.application.AuthFacade
import com.dh.baro.identity.application.dto.LoginResponse
import com.dh.baro.identity.presentation.dto.OauthLoginRequest
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authFacade: AuthFacade
) {

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login/oauth")
    fun loginWithOauth(
        @RequestBody oauthLoginRequest: OauthLoginRequest,
        request: HttpServletRequest
    ): LoginResponse {
        val result = authFacade.login(oauthLoginRequest.provider, oauthLoginRequest.accessToken)
        request.session.apply {
            setAttribute(USER_ID, result.userId)
            setAttribute(USER_ROLE, result.userRole)
        }

        return LoginResponse(result.isNew)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/logout")
    fun logout(request: HttpServletRequest) {
        request.session.invalidate()
    }
}
