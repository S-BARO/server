package com.dh.baro.identity.presentation

import com.dh.baro.identity.application.AuthFacade
import com.dh.baro.identity.application.dto.LoginResponse
import com.dh.baro.identity.domain.AuthProvider
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
        @RequestParam("provider") provider: AuthProvider,
        @RequestParam("access_token") accessToken: String,
        request: HttpServletRequest
    ): LoginResponse {
        val result = authFacade.login(provider, accessToken)
        request.session.apply { setAttribute(MEMBER_ID, result.memberId) }
        return LoginResponse(result.isNew)
    }

    companion object SessionKeys {
        const val MEMBER_ID = "MEMBER_ID"
    }
}
