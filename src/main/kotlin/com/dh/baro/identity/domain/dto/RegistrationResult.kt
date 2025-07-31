package com.dh.baro.identity.domain.dto

import com.dh.baro.identity.domain.MemberRole

data class RegistrationResult(
    val memberId: Long,
    val memberRole: MemberRole,
    val isNew: Boolean,
)
