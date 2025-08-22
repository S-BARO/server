package com.dh.baro.test

import com.dh.baro.core.auth.SessionKeys.USER_ID
import com.dh.baro.core.auth.SessionKeys.USER_ROLE
import com.dh.baro.identity.domain.UserRole
import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@Profile(value = ["local", "dev"])
@RestController
@RequestMapping("/test")
class TestController(
) {

    @PostMapping("/login/1")
    @ResponseStatus(HttpStatus.CREATED)
    fun issueAdminSession(request: HttpServletRequest) {
        request.session.apply {
            setAttribute(USER_ID, 1L)
            setAttribute(USER_ROLE, UserRole.ADMIN)
        }
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun login(@RequestParam userId: Long, request: HttpServletRequest) {
        request.session.apply {
            setAttribute(USER_ID, userId)
            setAttribute(USER_ROLE, UserRole.ADMIN)
        }
    }
}
