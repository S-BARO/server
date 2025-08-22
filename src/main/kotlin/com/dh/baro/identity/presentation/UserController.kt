package com.dh.baro.identity.presentation

import com.dh.baro.core.annotation.CurrentUser
import com.dh.baro.core.annotation.CheckAuth
import com.dh.baro.identity.application.UserFacade
import com.dh.baro.identity.domain.UserRole
import com.dh.baro.identity.presentation.dto.UserProfileResponse
import com.dh.baro.identity.presentation.swagger.UserSwagger
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
}
