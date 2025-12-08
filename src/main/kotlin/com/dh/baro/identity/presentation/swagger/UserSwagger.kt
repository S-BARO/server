package com.dh.baro.identity.presentation.swagger

import com.dh.baro.core.ErrorResponse
import com.dh.baro.identity.presentation.dto.UserProfileResponse
import com.dh.baro.identity.presentation.dto.UserProfileUpdateRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
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
                                  "phoneNumber": "01012345678",
                                  "address": "경기도 용인시 기흥구",
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

    /* ──────────────────────────────── 내 프로필 수정 ──────────────────────────────── */
    @Operation(
        summary = "내 프로필 수정",
        description = """
            현재 로그인한 회원의 프로필 정보를 수정합니다.
            수정 가능한 항목: phoneNumber, address
        """,
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = [
                Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = UserProfileUpdateRequest::class),
                    examples = [
                        ExampleObject(
                            name = "updateProfile",
                            value = """
                            {
                              "phoneNumber": "01098765432",
                              "address": "서울시 강남구 테헤란로"
                            }
                            """
                        )
                    ]
                )
            ]
        ),
        responses = [
            /* 200 */
            ApiResponse(
                responseCode = "200",
                description = "수정 성공",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = UserProfileResponse::class),
                        examples = [
                            ExampleObject(
                                name = "updatedProfile",
                                value = """
                                {
                                  "id": 1,
                                  "name": "홍길동",
                                  "email": "hong@gildong.dev",
                                  "phoneNumber": "01098765432",
                                  "address": "서울시 강남구 테헤란로",
                                  "role": "BUYER"
                                }
                                """
                            )
                        ]
                    )
                ]
            ),
            /* 400 */
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청(유효성 검증 실패)",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "validationError",
                                value = """
                                {
                                  "message": "전화번호는 최대 11자까지 입력 가능합니다."
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
    @PatchMapping("/me")
    fun updateUserProfile(
        @Parameter(hidden = true) userId: Long,
        @RequestBody request: UserProfileUpdateRequest,
    ): UserProfileResponse
}
