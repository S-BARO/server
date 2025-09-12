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

    fun getCurrentUserId(): Long {
        val session = request.getSession(false)
        val userId = session?.getAttribute(SessionKeys.USER_ID)

        return userId as? Long
            ?: throw UnauthorizedException(ErrorMessage.UNAUTHORIZED.message)
    }

    fun getCurrentUserRole(): UserRole =
        request.getSession(false)
            ?.getAttribute(SessionKeys.USER_ROLE) as? UserRole
            ?: throw UnauthorizedException(ErrorMessage.UNAUTHORIZED.message)
}
