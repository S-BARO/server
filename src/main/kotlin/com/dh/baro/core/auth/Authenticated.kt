package com.dh.baro.core.auth

import com.dh.baro.identity.domain.MemberRole

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Authenticated(
    val roles: Array<MemberRole> = emptyArray()
)
