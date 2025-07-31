package com.dh.baro.identity.presentation

import com.dh.baro.core.auth.SessionKeys.MEMBER_ID
import com.dh.baro.core.auth.SessionKeys.MEMBER_ROLE
import com.dh.baro.identity.application.AuthFacade
import com.dh.baro.identity.application.dto.LoginResponse
import com.dh.baro.identity.domain.AuthProvider
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
            setAttribute(MEMBER_ID, result.memberId)
            setAttribute(MEMBER_ROLE, result.memberRole)
        }

        return LoginResponse(result.isNew)
    }

    companion object SessionKeys {
        const val MEMBER_ID = "MEMBER_ID"
    }
}
