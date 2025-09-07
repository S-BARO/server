package com.dh.baro.identity.presentation.dto

import com.dh.baro.identity.domain.User
import com.dh.baro.identity.domain.UserRole

data class UserProfileResponse(
    val id: String,
    val name: String,
    val email: String,
    val phoneNumber: String?,
    val address: String?,
    val role: UserRole,
) {

    companion object {
        fun from(user: User) = UserProfileResponse(
            id = user.id.toString(),
            name = user.getName(),
            email = user.getEmail(),
            phoneNumber = user.getPhoneNumber(),
            address = user.getAddress(),
            role = user.role,
        )
    }
}
