package com.dh.baro.identity.domain.dto

import com.dh.baro.identity.domain.UserRole

data class RegistrationResult(
    val userId: Long,
    val userRole: UserRole,
    val isNew: Boolean,
)
