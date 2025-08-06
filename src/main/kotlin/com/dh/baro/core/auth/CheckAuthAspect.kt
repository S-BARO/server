package com.dh.baro.core.auth

import com.dh.baro.core.ErrorMessage
import com.dh.baro.core.exception.ForbiddenException
import com.dh.baro.identity.domain.UserRole
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Aspect
@Component
class CheckAuthAspect(
    private val sessionManager: SessionManager
) {

    @Around("@within(auth) || @annotation(auth)")
    @Transactional(readOnly = true)
    fun checkAuthentication(joinPoint: ProceedingJoinPoint, auth: CheckAuth): Any? {
        sessionManager.getCurrentUserId()
        if (auth.roles.isNotEmpty()) {
            val role = sessionManager.getCurrentUserRole()
            if (role != UserRole.ADMIN && role !in auth.roles) {
                throw ForbiddenException(ErrorMessage.FORBIDDEN.message)
            }
        }

        return joinPoint.proceed()
    }
}
