package com.dh.baro.product.presentation.swagger

import com.dh.baro.core.SliceResponse
import com.dh.baro.core.Cursor
import com.dh.baro.product.presentation.dto.*
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
    name = "Product API",
    description = "상품(Product) 관련 API입니다. 상품 조회 관련 API는 인증없이 사용가능합니다."
)
@RequestMapping("/products")
interface ProductSwagger {

    /* ───────────────────────────── 상품 생성 ───────────────────────────── */
    @Operation(
        summary = "상품 생성(STORE_OWNER 전용)",
        description = "신규 상품을 등록합니다. STORE_OWNER 권한이 필요합니다",
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = [Content(
                schema = Schema(implementation = ProductCreateRequest::class),
                examples = [ExampleObject(
                    name = "createRequest",
                    value = """
                    {
                      "name": "T-Shirt",
                      "storeId": 123,
                      "price": 19900,
                      "quantity": 100,
                      "description": "면 100% 기본 티셔츠",
                      "likesCount": 0,
                      "thumbnailUrl": "https://example.com/11-thumb.jpg",
                      "categoryIds": [1, 2],
                      "imageUrls": ["https://example.com/11-thumb.jpg"]
                    }
                    """
                )]
            )]
        ),
        responses = [
            ApiResponse(
                responseCode = "201", description = "생성 성공",
                content = [Content(schema = Schema(implementation = ProductCreateResponse::class))]
            )
        ]
    )
    @PostMapping
    fun createProduct(
        @RequestBody request: ProductCreateRequest,
    ): ProductCreateResponse

    /* ───────────────────────────── 상품 상세 ───────────────────────────── */
    @Operation(
        summary = "상품 상세 보기",
        description = "특정 상품에 대한 상세 정보를 불러옵니다.",
        parameters = [Parameter(`in` = ParameterIn.PATH, name = "productId", description = "상품 PK", example = "11", required = true)],
        responses = [
            ApiResponse(
                responseCode = "200", description = "조회 성공",
                content = [Content(schema = Schema(implementation = ProductDetail::class))]
            )
        ]
    )
    @GetMapping("/{productId}")
    fun getProductDetail(@PathVariable productId: String): ProductDetail

    /* ───────────────────────────── 인기 상품 ───────────────────────────── */
    @Operation(
        summary = "인기 상품 목록(무한 스크롤)",
        description = """
            좋아요(likesCount) DESC → id DESC 로 정렬된 상품을 무한스크롤 방식으로 반환합니다.
            categoryId : 카테고리 필터 (필수아님)
            cursorLikes, cursorId : 이전 페이지의 마지막 요소에서 받은 커서 값 (필수 아님 / 단, 둘 다 넣거나 둘 다 제외하거나)
            size : 페이지 크기(기본값 21)
        """,
        parameters = [
            Parameter(`in` = ParameterIn.QUERY, name = "categoryId", description = "카테고리 PK", example = "1"),
            Parameter(`in` = ParameterIn.QUERY, name = "cursorId", description = "마지막 상품 id", example = "11"),
            Parameter(`in` = ParameterIn.QUERY, name = "cursorLikes", description = "마지막 상품 likes", example = "300"),
            Parameter(`in` = ParameterIn.QUERY, name = "size", description = "페이지 크기", example = "21")
        ],
        responses = [
            ApiResponse(
                responseCode = "200", description = "조회 성공",
                content = [Content(
                    schema = Schema(implementation = SlicePopularExample::class),
                    examples = [ExampleObject(
                        name = "popularResponse",
                        value = """
                        {
                          "content": [
                            { "id": 21, "storeName": "무신사", "productName": "Sneakers", "price": 99000, "thumbnailUrl": "..." }
                          ],
                          "hasNext": true,
                          "nextCursor": { "id": 21, "likes": 500 }
                        }
                        """
                    )]
                )]
            )
        ]
    )
    @GetMapping("/popular")
    fun getPopularProducts(
        @RequestParam(required = false) categoryId: Long?,
        @RequestParam(required = false) cursorId: Long?,
        @RequestParam(required = false) cursorLikes: Int?,
        @RequestParam(defaultValue = "21") size: Int
    ): SliceResponse<ProductListItem>

    /* ───────────────────────────── 최신 상품 ───────────────────────────── */
    @Operation(
        summary = "최신 상품(최근 30일) 목록",
        description = """
            최근 30일 이내 상품을 최신순으로 반환합니다.
            categoryId : 카테고리 필터 (필수아님)
            cursorId : 이전 페이지의 마지막 요소에서 받은 커서 값 (필수 아님)
            size : 페이지 크기(기본값 21)
        """,
        parameters = [
            Parameter(`in` = ParameterIn.QUERY, name = "categoryId", description = "카테고리 PK", example = "1"),
            Parameter(`in` = ParameterIn.QUERY, name = "cursorId", description = "마지막 상품 id", example = "21"),
            Parameter(`in` = ParameterIn.QUERY, name = "size", description = "페이지 크기", example = "21")
        ],
        responses = [
            ApiResponse(
                responseCode = "200", description = "조회 성공",
                content = [Content(
                    schema = Schema(implementation = SliceNewestExample::class),
                    examples = [ExampleObject(
                        name = "newestResponse",
                        value = """
                        {
                          "content": [
                            { "id": 12, "storeName": "무신사", "productName": "Hoodie", "price": 59000, "thumbnailUrl": "..." }
                          ],
                          "hasNext": false,
                          "nextCursor": { "id": 11 }
                        }
                        """
                    )]
                )]
            )
        ]
    )
    @GetMapping("/newest")
    fun getNewestProducts(
        @RequestParam(required = false) categoryId: Long?,
        @RequestParam(required = false) cursorId: Long?,
        @RequestParam(defaultValue = "21") size: Int
    ): SliceResponse<ProductListItem>

    /* ──────────────── 예시 DTO (Swagger 문서 전용) ──────────────── */
    @Schema(hidden = true)
    private class SlicePopularExample(
        val content: List<ProductListItem> = emptyList(),
        val hasNext: Boolean = false,
        val nextCursor: PopularCursor? = null
    )

    @Schema(hidden = true)
    private class SliceNewestExample(
        val content: List<ProductListItem> = emptyList(),
        val hasNext: Boolean = false,
        val nextCursor: Cursor? = null
    )
}
