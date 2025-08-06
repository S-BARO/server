package com.dh.baro.identity.presentation.swagger

import com.dh.baro.core.ErrorResponse
import com.dh.baro.identity.presentation.dto.OauthLoginRequest
import com.dh.baro.identity.application.dto.LoginResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.*

@Tag(
    name = "Auth API",
    description = "인증(Auth) 관련 API입니다."
)
@RequestMapping("/auth")
interface AuthSwagger {

    /* ─────────────────────────────── 1. OAuth 로그인 ─────────────────────────────── */
    @Operation(
        summary = "OAuth 로그인(Session 발급)",
        description = "외부 OAuth 공급자(Kakao 등)의 accessToken을 검증해 세션(쿠키)을 생성합니다.",
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = [Content(
                schema = Schema(implementation = OauthLoginRequest::class),
                examples = [ExampleObject(
                    name = "loginRequest",
                    value = """
                    {
                      "provider": "KAKAO",
                      "accessToken": "ya29.a0AfH6SMD..."
                    }
                    """
                )]
            )]
        ),
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "로그인 성공",
                content = [Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = LoginResponse::class),
                    examples = [ExampleObject(
                        name = "loginSuccess",
                        value = """
                        {
                          "isNew": false
                        }
                        """
                    )]
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 / 토큰 검증 실패",
                content = [Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class),
                    examples = [ExampleObject(
                        name = "invalidToken",
                        value = """
                        {
                          "message": "유효하지 않은 accessToken 입니다."
                        }
                        """
                    )]
                )]
            )
        ]
    )
    @PostMapping("/login/oauth")
    fun loginWithOauth(
        @RequestBody oauthLoginRequest: OauthLoginRequest,
        @Parameter(hidden = true) request: HttpServletRequest
    ): LoginResponse

    /* ─────────────────────────────── 2. 로그아웃 ─────────────────────────────── */
    @Operation(
        summary = "로그아웃(Session 만료)",
        description = "현재 세션을 무효화합니다.",
        responses = [
            ApiResponse(responseCode = "204", description = "로그아웃 완료"),
            ApiResponse(
                responseCode = "401",
                description = "로그인 상태가 아님",
                content = [Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class),
                    examples = [ExampleObject(
                        value = """
                        {
                          "message": "세션이 존재하지 않습니다."
                        }
                        """
                    )]
                )]
            )
        ]
    )
    @PostMapping("/logout")
    fun logout(
        @Parameter(hidden = true) request: HttpServletRequest
    )

    @Operation(
        summary = "ADMIN 세션 발급 (테스트 전용)",
        description = "세션에 **USER_ID=1**, **USER_ROLE=ADMIN** 값을 저장하고 쿠키로 발급합니다.",
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "세션 발급 성공 (쿠키 포함)",
                content = [Content(mediaType = APPLICATION_JSON_VALUE)]
            )
        ]
    )
    @PostMapping("/login/test")
    fun issueAdminSession(
        @Parameter(hidden = true) request: HttpServletRequest,
    )
}
