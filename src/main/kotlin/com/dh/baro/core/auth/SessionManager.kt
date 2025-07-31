package com.dh.baro.core.auth

import com.dh.baro.core.ErrorMessage
import com.dh.baro.core.exception.UnauthorizedException
import com.dh.baro.identity.domain.MemberRole
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component

@Component
class SessionManager(
    private val request: HttpServletRequest
) {

    fun getCurrentMemberId(): Long =
        request.getSession(false)
            ?.getAttribute(SessionKeys.MEMBER_ID) as? Long
            ?: throw UnauthorizedException(ErrorMessage.UNAUTHORIZED.message)

    fun getCurrentMemberRole(): MemberRole =
        request.getSession(false)
            ?.getAttribute(SessionKeys.MEMBER_ROLE) as? MemberRole
            ?: throw UnauthorizedException(ErrorMessage.UNAUTHORIZED.message)
}
