package com.dh.baro.identity.application

import com.dh.baro.identity.domain.User
import com.dh.baro.identity.domain.service.UserService
import org.springframework.stereotype.Service

@Service
class UserFacade (
    private val userService: UserService,
){
    fun getUserById(userId: Long): User =
        userService.getUserById(userId)
}
