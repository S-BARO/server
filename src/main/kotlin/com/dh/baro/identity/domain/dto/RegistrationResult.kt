package com.dh.baro.identity.domain.dto

data class RegistrationResult(
    val memberId: Long,
    val memberRole: String,
    val isNew: Boolean,
)
