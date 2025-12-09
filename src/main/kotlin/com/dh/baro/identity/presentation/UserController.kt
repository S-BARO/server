package com.dh.baro.identity.presentation

import com.dh.baro.core.annotation.CurrentUser
import com.dh.baro.core.annotation.CheckAuth
import com.dh.baro.identity.application.UserFacade
import com.dh.baro.identity.domain.UserRole
import com.dh.baro.identity.presentation.dto.AddressUpdateRequest
import com.dh.baro.identity.presentation.dto.PhoneNumberUpdateRequest
import com.dh.baro.identity.presentation.dto.UserProfileResponse
import com.dh.baro.identity.presentation.dto.UserProfileUpdateRequest
import com.dh.baro.identity.presentation.swagger.UserSwagger
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
@CheckAuth(UserRole.BUYER, UserRole.STORE_OWNER)
class UserController(
    private val userFacade: UserFacade,
) : UserSwagger {

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    override fun getUserProfile(@CurrentUser userId: Long): UserProfileResponse =
        UserProfileResponse.from(userFacade.getUserById(userId))

    @PatchMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    override fun updateUserProfile(
        @CurrentUser userId: Long,
        @Valid @RequestBody request: UserProfileUpdateRequest,
    ): UserProfileResponse {
        val updatedUser = userFacade.updateUserProfile(userId, request.phoneNumber, request.address)
        return UserProfileResponse.from(updatedUser)
    }

    @PatchMapping("/me/phone-number")
    @ResponseStatus(HttpStatus.OK)
    override fun updatePhoneNumber(
        @CurrentUser userId: Long,
        @Valid @RequestBody request: PhoneNumberUpdateRequest,
    ): UserProfileResponse {
        val updatedUser = userFacade.updatePhoneNumberOnly(userId, request.phoneNumber)
        return UserProfileResponse.from(updatedUser)
    }

    @PatchMapping("/me/address")
    @ResponseStatus(HttpStatus.OK)
    override fun updateAddress(
        @CurrentUser userId: Long,
        @Valid @RequestBody request: AddressUpdateRequest,
    ): UserProfileResponse {
        val updatedUser = userFacade.updateAddressOnly(userId, request.address)
        return UserProfileResponse.from(updatedUser)
    }
}
