package com.dh.baro.identity.presentation.swagger

import com.dh.baro.core.ErrorResponse
import com.dh.baro.identity.presentation.dto.UserProfileResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Tag(
    name = "User API",
    description = "회원(User) 관련 API입니다."
)
@RequestMapping("/users")
interface UserSwagger {

    /* ──────────────────────────────── 내 프로필 조회 ──────────────────────────────── */
    @Operation(
        summary = "내 프로필 조회",
        description = "현재 로그인한 회원의 상세 프로필 정보를 반환합니다.",
        responses = [
            /* 200 */
            ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = UserProfileResponse::class),
                        examples = [
                            ExampleObject(
                                name = "userProfile",
                                value = """
                                {
                                  "id": 1,
                                  "name": "홍길동",
                                  "email": "hong@gildong.dev",
                                  "phoneNumber": "010-1234-5678",
                                  "role": "BUYER"
                                }
                                """
                            )
                        ]
                    )
                ]
            ),
            /* 401 */
            ApiResponse(
                responseCode = "401",
                description = "미인증(세션/토큰 없음)",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "unauthorized",
                                value = """
                                {
                                  "message": "로그인이 필요합니다."
                                }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @GetMapping("/me")
    fun getUserProfile(
        @Parameter(hidden = true) userId: Long,
    ): UserProfileResponse
}
