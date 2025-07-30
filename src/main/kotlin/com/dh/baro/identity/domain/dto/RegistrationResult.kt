package com.dh.baro.identity.domain.dto

import com.dh.baro.identity.domain.Member

data class RegistrationResult(
    val member: Member,
    val isNew: Boolean,
)
