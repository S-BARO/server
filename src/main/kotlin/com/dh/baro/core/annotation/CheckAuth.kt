package com.dh.baro.core.annotation

import com.dh.baro.identity.domain.UserRole

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CheckAuth(
    vararg val roles: UserRole = []
)
