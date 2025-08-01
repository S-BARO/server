package com.dh.baro.identity.application.dto

import com.dh.baro.identity.domain.UserRole

data class AuthResult(
    val userId: Long,
    val userRole: UserRole,
    val isNew: Boolean,
)
