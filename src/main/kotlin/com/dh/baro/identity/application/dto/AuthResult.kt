package com.dh.baro.identity.application.dto

data class AuthResult(
    val memberId: Long,
    val memberRole: String,
    val isNew: Boolean,
)
