package com.dh.baro.identity.presentation.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class PhoneNumberUpdateRequest(
    @field:NotBlank(message = "전화번호는 필수 입력 항목입니다.")
    @field:Size(max = 11, message = "전화번호는 최대 11자까지 입력 가능합니다.")
    val phoneNumber: String,
)
