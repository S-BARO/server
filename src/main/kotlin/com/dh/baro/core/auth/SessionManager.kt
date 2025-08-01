package com.dh.baro.core.auth

import com.dh.baro.core.ErrorMessage
import com.dh.baro.core.exception.UnauthorizedException
import com.dh.baro.identity.domain.UserRole
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component

@Component
class SessionManager(
    private val request: HttpServletRequest
) {

    fun getCurrentUserId(): Long =
        request.getSession(false)
            ?.getAttribute(SessionKeys.USER_ID) as? Long
            ?: throw UnauthorizedException(ErrorMessage.UNAUTHORIZED.message)

    fun getCurrentUserRole(): UserRole =
        request.getSession(false)
            ?.getAttribute(SessionKeys.USER_ROLE) as? UserRole
            ?: throw UnauthorizedException(ErrorMessage.UNAUTHORIZED.message)
}
