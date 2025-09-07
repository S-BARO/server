package com.dh.baro.look.presentation.swagger

import com.dh.baro.core.Cursor
import com.dh.baro.core.SliceResponse
import com.dh.baro.look.presentation.dto.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@Tag(
    name = "Look API",
    description = "룩(Look) 관련 API입니다."
)
@RequestMapping("/looks")
interface LookSwagger {

    /* ───────────────────────────── 룩 생성 ───────────────────────────── */
    @Operation(
        summary = "룩 생성",
        description = """
            새로운 코디 룩을 생성합니다.
            imageUrls 는 입력 순서대로 displayOrder가 1부터 부여됩니다.
            productIds는 최초애 등록된 순서를 유지합니다.
        """,
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = [Content(
                schema = Schema(implementation = LookCreateRequest::class),
                examples = [ExampleObject(
                    name = "createLookRequest",
                    value = """
                    {
                      "title": "여름 바캉스룩",
                      "description": "린넨 셔츠 + 쇼츠 + 로퍼",
                      "thumbnailUrl": "https://example.com/look-thumb.jpg",
                      "imageUrls": ["https://example.com/look-1.jpg", "https://example.com/look-2.jpg"],
                      "productIds": [101, 102, 103]
                    }
                    """
                )]
            )]
        ),
        responses = [
            ApiResponse(
                responseCode = "201", description = "생성 성공",
                content = [Content(schema = Schema(implementation = LookCreateResponse::class))]
            )
        ]
    )
    @PostMapping
    fun createLook(
        @Parameter(hidden = true) userId: Long,
        @RequestBody request: LookCreateRequest,
    ): LookCreateResponse

    /* ───────────────── 룩 반응(좋아요/싫어요) 등록/대체(멱등) ───────────────── */
    @Operation(
        summary = "룩 반응 등록(좋아요/싫어요)",
        description = """
            사용자의 해당 룩에 대한 반응을 기록합니다.
            같은 반응을 반복 요청해도 멱등(상태 변화 없음)입니다.
            LIKE 반응의 최초 등록 시 likesCount가 1 증가합니다.
            reationType = [LIKE, DISLIKE]
        """,
        parameters = [
            Parameter(`in` = ParameterIn.PATH, name = "lookId", description = "룩 ID", required = true, example = "1001")
        ],
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = [Content(
                schema = Schema(implementation = ReactionRequest::class),
                examples = [ExampleObject(
                    name = "likeRequest",
                    value = """{ "reactionType": "LIKE" }"""
                ), ExampleObject(
                    name = "dislikeRequest",
                    value = """{ "reactionType": "DISLIKE" }"""
                )]
            )]
        ),
        responses = [
            ApiResponse(responseCode = "204", description = "성공 (본문 없음)")
        ]
    )
    @PutMapping("/{lookId}/reaction")
    fun recordReaction(
        @Parameter(hidden = true) userId: Long,
        @PathVariable lookId: String,
        @RequestBody request: ReactionRequest,
    )

    /* ──────────────────────── 룩 반응 취소(좋아요/싫어요 제거) ─────────────────────── */
    @Operation(
        summary = "룩 반응 취소",
        description = """
            사용자의 해당 룩에 대한 반응(좋아요/싫어요)을 취소합니다.
            LIKE 취소 시에만 likesCount가 1 감소합니다.
            반응이 없어도 멱등하게 204를 반환합니다.
        """,
        parameters = [
            Parameter(`in` = ParameterIn.PATH, name = "lookId", description = "룩 ID", required = true, example = "1001")
        ],
        responses = [
            ApiResponse(responseCode = "204", description = "성공 (본문 없음)")
        ]
    )
    @DeleteMapping("/{lookId}/reaction")
    fun cancelReaction(
        @Parameter(hidden = true) userId: Long,
        @PathVariable lookId: String,
    )

    /* ───────────────────────── 스와이프 피드(무한 스크롤) ───────────────────────── */
    @Operation(
        summary = "스와이프용 룩 목록(무한 스크롤)",
        description = """
            로그인 사용자가 아직 반응(좋아요/싫어요)하지 않은 룩을 최신순으로 반환합니다.
            커서 기반 페이지네이션:
            cursorId: 이전 페이지의 마지막 룩 ID
            size: 페이지 크기(기본 10)
        """,
        parameters = [
            Parameter(
                `in` = ParameterIn.QUERY,
                name = "cursorId",
                description = "마지막 룩 ID",
                required = false,
                example = "1234"
            ),
            Parameter(`in` = ParameterIn.QUERY, name = "size", description = "페이지 크기", required = false, example = "10")
        ],
        responses = [
            ApiResponse(
                responseCode = "200", description = "조회 성공",
                content = [Content(
                    schema = Schema(implementation = SwipeSliceExample::class),
                    examples = [ExampleObject(
                        name = "swipeLooksResponse",
                        value = """
                        {
                          "content": [
                            { "lookId": 2003, "title": "가을 캠퍼스룩", "thumbnailUrl": "https://..." },
                            { "lookId": 2002, "title": "주말 데이트룩", "thumbnailUrl": "https://..." }
                          ],
                          "hasNext": true,
                          "nextCursor": { "id": 2002 }
                        }
                        """
                    )]
                )]
            )
        ]
    )
    @GetMapping("/swipe")
    fun getSwipeLooks(
        @Parameter(hidden = true) userId: Long,
        @RequestParam(required = false) cursorId: String?,
        @RequestParam(defaultValue = "10") size: Int,
    ): SliceResponse<LookDto>

    /* ───────────────────────────── 룩 상세 ───────────────────────────── */
    @Operation(
        summary = "룩 상세 조회",
        description = """
            룩 기본정보 + 이미지(정렬 포함) + 구성 상품(정렬 포함)을 반환합니다.
        """,
        parameters = [
            Parameter(`in` = ParameterIn.PATH, name = "lookId", description = "룩 ID", required = true, example = "1001")
        ],
        responses = [
            ApiResponse(
                responseCode = "200", description = "조회 성공",
                content = [Content(
                    schema = Schema(implementation = LookDetailResponse::class),
                    examples = [ExampleObject(
                        name = "lookDetailResponse",
                        value = """
                        {
                          "lookId": 1001,
                          "title": "여름 바캉스룩",
                          "description": "린넨 셔츠 + 쇼츠 + 로퍼",
                          "thumbnailUrl": "https://example.com/look-thumb.jpg",
                          "likesCount": 42,
                          "images": [
                            { "imageUrl": "https://example.com/look-1.jpg", "displayOrder": 1 },
                            { "imageUrl": "https://example.com/look-2.jpg", "displayOrder": 2 }
                          ],
                          "products": [
                            { "productId": 101, "name": "린넨 셔츠", "price": 39000, "thumbnailUrl": "https://...", "displayOrder": 1 },
                            { "productId": 102, "name": "베이지 쇼츠", "price": 29000, "thumbnailUrl": "https://...", "displayOrder": 2 }
                          ]
                        }
                        """
                    )]
                )]
            )
        ]
    )
    @GetMapping("/{lookId}")
    fun getLookDetail(@PathVariable lookId: String): LookDetailResponse

    /* ──────────────── 문서 전용 예시 DTO ─────────────── */
    @Schema(hidden = true)
    private class SwipeSliceExample(
        val content: List<LookDto> = emptyList(),
        val hasNext: Boolean = false,
        val nextCursor: Cursor? = null
    )
}
