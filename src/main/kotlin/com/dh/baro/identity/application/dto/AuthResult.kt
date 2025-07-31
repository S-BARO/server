package com.dh.baro.identity.application.dto

import com.dh.baro.identity.domain.MemberRole

data class AuthResult(
    val memberId: Long,
    val memberRole: MemberRole,
    val isNew: Boolean,
)
