package com.dh.baro.identity.presentation.dto

import jakarta.validation.constraints.Size

data class UserProfileUpdateRequest(
    @field:Size(max = 11, message = "전화번호는 최대 11자까지 입력 가능합니다.")
    val phoneNumber: String? = null,

    @field:Size(max = 500, message = "주소는 최대 500자까지 입력 가능합니다.")
    val address: String? = null,
)
