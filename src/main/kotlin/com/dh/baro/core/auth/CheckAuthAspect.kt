package com.dh.baro.core.auth

import com.dh.baro.core.ErrorMessage
import com.dh.baro.core.anotation.CheckAuth
import com.dh.baro.core.exception.ForbiddenException
import com.dh.baro.identity.domain.UserRole
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class CheckAuthAspect(
    private val sessionManager: SessionManager
) {

    @Around("@within(auth)")
    fun checkOnClass(pjp: ProceedingJoinPoint, auth: CheckAuth) = doCheck(pjp, auth)

    @Around("@annotation(auth)")
    fun checkOnMethod(pjp: ProceedingJoinPoint, auth: CheckAuth) = doCheck(pjp, auth)

    private fun doCheck(pjp: ProceedingJoinPoint, auth: CheckAuth): Any? {
        sessionManager.getCurrentUserId()

        if (auth.roles.isNotEmpty()) {
            val role = sessionManager.getCurrentUserRole()
            if (role != UserRole.ADMIN && role !in auth.roles) {
                throw ForbiddenException(ErrorMessage.FORBIDDEN.message)
            }
        }

        return pjp.proceed()
    }
}
