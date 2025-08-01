package com.dh.baro.core.auth

import com.dh.baro.identity.domain.UserRole

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Authenticated(
    val roles: Array<UserRole> = []
)
