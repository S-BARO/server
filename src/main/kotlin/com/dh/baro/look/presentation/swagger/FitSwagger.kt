package com.dh.baro.look.presentation.swagger

import com.dh.baro.core.ErrorResponse
import com.dh.baro.look.presentation.dto.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.parameters.RequestBody
import jakarta.validation.Valid
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@Tag(
    name = "Fit API",
    description = "피팅(Fit) 관련 API입니다. 사용자가 피팅 소스 이미지를 업로드하고 관리할 수 있습니다."
)
interface FitSwagger {

    /* ───────────────────────────── 피팅 소스 이미지 업로드 URL 생성 ───────────────────────────── */
    @Operation(
        summary = "피팅 소스 이미지 업로드 URL 생성",
        description = """
            사용자가 피팅 소스 이미지를 S3에 직접 업로드할 수 있는 presigned URL을 생성합니다.
            생성된 URL은 제한된 시간 동안만 유효하며, 지정된 파일 크기와 타입 제한이 적용됩니다.
            업로드 후에는 completeImageUpload API를 호출하여 업로드 완료를 처리해야 합니다.
        """,
        responses = [
            /* 200 */
            ApiResponse(
                responseCode = "200",
                description = "업로드 URL 생성 성공",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = FittingSourceImageUploadUrlResponse::class),
                        examples = [
                            ExampleObject(
                                name = "uploadUrlResponse",
                                value = """
                                {
                                  "imageId": 12345,
                                  "presignedUrl": "https://baro-bucket.s3.ap-northeast-2.amazonaws.com/fitting-images/12345?X-Amz-Algorithm=AWS4-HMAC-SHA256...",
                                  "expiresAt": "2024-01-15T10:30:00Z",
                                  "maxFileSize": 10485760,
                                  "allowedTypes": ["image/jpeg", "image/png", "image/webp"]
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
            ),
            /* 500 */
            ApiResponse(
                responseCode = "500",
                description = "S3 URL 생성 실패",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "s3Error",
                                value = """
                                {
                                  "message": "이미지 업로드 URL 생성에 실패했습니다."
                                }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    fun createUploadUrl(
        @Parameter(hidden = true) userId: Long
    ): FittingSourceImageUploadUrlResponse

    /* ───────────────────────────── 피팅 소스 이미지 업로드 완료 ───────────────────────────── */
    @Operation(
        summary = "피팅 소스 이미지 업로드 완료",
        description = """
            S3에 업로드된 피팅 소스 이미지를 데이터베이스에 저장하고 상태를 COMPLETED로 변경합니다.
            createUploadUrl API로 생성된 imageId를 사용하여 업로드 완료를 처리합니다.
            업로드가 완료되면 해당 이미지는 피팅 기능에서 사용할 수 있습니다.
        """,
        parameters = [
            Parameter(
                `in` = ParameterIn.PATH,
                name = "imageId",
                description = "업로드할 이미지의 고유 ID (createUploadUrl에서 반환된 값)",
                example = "12345",
                required = true
            )
        ],
        responses = [
            /* 204 */
            ApiResponse(
                responseCode = "204",
                description = "이미지 저장 완료"
            ),
            /* 400 */
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (이미지 ID 불일치 등)",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "invalidImageId",
                                value = """
                                {
                                  "message": "유효하지 않은 이미지 ID입니다."
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
            ),
            /* 404 */
            ApiResponse(
                responseCode = "404",
                description = "피팅 소스 이미지를 찾을 수 없음",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "imageNotFound",
                                value = """
                                {
                                  "message": "피팅 소스 이미지를 찾을 수 없습니다."
                                }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    fun completeImageUpload(
        @Parameter(hidden = true) userId: Long,
        @Parameter(
            `in` = ParameterIn.PATH,
            name = "imageId",
            description = "완료할 이미지의 고유 ID",
            example = "12345",
            required = true
        ) imageId: Long
    )

    /* ───────────────────────────── 사용자 피팅 소스 이미지 목록 조회 ───────────────────────────── */
    @Operation(
        summary = "사용자 피팅 소스 이미지 목록 조회",
        description = """
            현재 로그인한 사용자가 업로드 완료한 피팅 소스 이미지 목록을 조회합니다.
            업로드가 완료된 (COMPLETED 상태) 이미지만 반환하며, 모든 이미지는 피팅 기능에서 즉시 사용할 수 있습니다.
            이미지는 업로드 일시 기준으로 내림차순 정렬되어 반환됩니다.
        """,
        responses = [
            /* 200 */
            ApiResponse(
                responseCode = "200",
                description = "이미지 목록 조회 성공",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = FittingSourceImageListResponse::class),
                        examples = [
                            ExampleObject(
                                name = "imageListResponse",
                                value = """
                                {
                                  "images": [
                                    {
                                      "id": 12345,
                                      "imageUrl": "https://baro-bucket.s3.ap-northeast-2.amazonaws.com/fitting-images/12345.jpg",
                                      "createdAt": "2024-01-15T09:30:00Z"
                                    },
                                    {
                                      "id": 12346,
                                      "imageUrl": "https://baro-bucket.s3.ap-northeast-2.amazonaws.com/fitting-images/12346.jpg",
                                      "createdAt": "2024-01-15T08:15:00Z"
                                    }
                                  ]
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
    fun getUserFittingSourceImages(
        @Parameter(hidden = true) userId: Long
    ): FittingSourceImageListResponse

    /* ───────────────────────────── AI 피팅 이미지 생성 ───────────────────────────── */
    @Operation(
        summary = "AI 피팅 이미지 생성",
        description = """
            사용자가 업로드한 피팅 소스 이미지와 의류 이미지를 사용하여 AI 피팅 이미지를 생성합니다.
            AI 피팅 기능은 크레딧 시스템을 통해 제한되며, 크레딧이 부족하면 요청이 거부됩니다.
            생성된 이미지는 PNG 형식의 바이너리 데이터로 반환됩니다.
        """,
        requestBody = RequestBody(
            description = "AI 피팅 요청 정보",
            required = true,
            content = [
                Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = AiFittingRequest::class),
                    examples = [
                        ExampleObject(
                            name = "aiFittingRequest",
                            value = """
                            {
                              "sourceImageUrl": "https://baro-bucket.s3.ap-northeast-2.amazonaws.com/fitting-images/12345.jpg",
                              "clothingImageUrl": "https://example.com/clothing/shirt-123.jpg"
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
                description = "AI 피팅 이미지 생성 성공",
                content = [
                    Content(
                        mediaType = "image/png",
                        schema = Schema(type = "string", format = "binary"),
                        examples = [
                            ExampleObject(
                                name = "fittingImage",
                                description = "생성된 피팅 이미지 (PNG 형식의 바이너리 데이터)"
                            )
                        ]
                    )
                ]
            ),
            /* 400 */
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (필수 필드 누락, 유효하지 않은 URL 등)",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "invalidRequest",
                                value = """
                                {
                                  "message": "요청 본문 필드 검증에 실패했습니다."
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
            ),
            /* 429 */
            ApiResponse(
                responseCode = "429",
                description = "AI 피팅 크레딧 부족 또는 사용량 제한 초과",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "rateLimitExceeded",
                                value = """
                                {
                                  "message": "AI 피팅 크레딧이 부족합니다. 잠시 후 다시 시도해주세요"
                                }
                                """
                            )
                        ]
                    )
                ]
            ),
            /* 500 */
            ApiResponse(
                responseCode = "500",
                description = "AI 피팅 생성 실패 (Gemini API 오류, 이미지 처리 오류 등)",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "aiFittingError",
                                value = """
                                {
                                  "message": "AI 피팅 이미지 생성 중 오류가 발생했습니다."
                                }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    fun generateAiFitting(
        @Parameter(hidden = true) userId: Long,
        @Valid request: AiFittingRequest
    ): org.springframework.http.ResponseEntity<ByteArray>
}
