package com.dh.baro.core.auth

import com.dh.baro.core.ErrorMessage
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.nio.file.AccessDeniedException

@Aspect
@Component
class AuthenticationAspect(
    private val sessionManager: SessionManager
) {

    @Around("@within(auth) || @annotation(auth)")
    @Transactional(readOnly = true)
    fun checkAuthentication(joinPoint: ProceedingJoinPoint, auth: Authenticated): Any? {
        sessionManager.getCurrentMemberId()
        if (auth.roles.isNotEmpty()) {
            val role = sessionManager.getCurrentMemberRole()
            if (role !in auth.roles) {
                throw AccessDeniedException(ErrorMessage.FORBIDDEN.message)
            }
        }

        return joinPoint.proceed()
    }
}
