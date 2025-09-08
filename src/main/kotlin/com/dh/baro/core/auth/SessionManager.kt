package com.dh.baro.core.auth

import com.dh.baro.core.ErrorMessage
import com.dh.baro.core.exception.UnauthorizedException
import com.dh.baro.identity.domain.UserRole
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SessionManager(
    private val request: HttpServletRequest
) {
    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    fun getCurrentUserId(): Long {
        val session = request.getSession(false)
        logger.info("Session exists: ${session != null}, sessionId: ${session?.id}")

        val userId = session?.getAttribute(SessionKeys.USER_ID)
        logger.info("Session getAttribute result: $userId, type: ${userId?.javaClass}")

        return userId as? Long
            ?: throw UnauthorizedException(ErrorMessage.UNAUTHORIZED.message)
    }

    fun getCurrentUserRole(): UserRole =
        request.getSession(false)
            ?.getAttribute(SessionKeys.USER_ROLE) as? UserRole
            ?: throw UnauthorizedException(ErrorMessage.UNAUTHORIZED.message)
}
