package com.dh.baro.identity.presentation.dto

import com.dh.baro.core.serialization.LongToStringSerializer
import com.dh.baro.identity.domain.User
import com.dh.baro.identity.domain.UserRole
import com.fasterxml.jackson.databind.annotation.JsonSerialize

data class UserProfileResponse(
    @JsonSerialize(using = LongToStringSerializer::class)
    val id: Long,
    val name: String,
    val email: String,
    val phoneNumber: String?,
    val address: String?,
    val role: UserRole,
) {

    companion object {
        fun from(user: User) = UserProfileResponse(
            id = user.id,
            name = user.getName(),
            email = user.getEmail(),
            phoneNumber = user.getPhoneNumber(),
            address = user.getAddress(),
            role = user.role,
        )
    }
}
