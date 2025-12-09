package com.dh.baro.identity.presentation.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AddressUpdateRequest(
    @field:NotBlank(message = "주소는 필수 입력 항목입니다.")
    @field:Size(max = 500, message = "주소는 최대 500자까지 입력 가능합니다.")
    val address: String,
)
