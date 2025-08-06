package com.dh.baro.cart.presentation.swagger

import com.dh.baro.cart.presentation.dto.*
import com.dh.baro.core.ErrorResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.*

@Tag(
    name = "Cart API",
    description = "장바구니(Cart) 관련 API입니다. BUYER 권한이 필요합니다."
)
@RequestMapping("/cart")
interface CartSwagger {

    /* ───────────────────────────────────── 장바구니 조회 ───────────────────────────────────── */
    @Operation(
        summary = "장바구니 조회",
        description = "현재 로그인 사용자의 장바구니를 반환합니다.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = [Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = CartResponse::class),
                    examples = [ExampleObject(
                        value = """
                        {
                          "items": [
                            {
                              "itemId": 101,
                              "productId": 11,
                              "productName": "T-Shirt",
                              "productThumbnailUrl": "https://example.com/11-thumb.jpg",
                              "price": 19900,
                              "quantity": 2,
                              "subtotal": 39800
                            }
                          ],
                          "totalPrice": 39800
                        }
                        """
                    )]
                )]
            )
        ]
    )
    @GetMapping
    fun getCart(
        @Parameter(hidden = true) userId: Long
    ): CartResponse

    /* ───────────────────────────────────── 상품 추가 ───────────────────────────────────── */
    @Operation(
        summary = "장바구니 상품 추가",
        description = "상품을 장바구니에 담습니다. 이미 담긴 상품이면 서버가 수량을 누적합니다.",
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = [Content(
                schema = Schema(implementation = AddItemRequest::class),
                examples = [ExampleObject(
                    value = """
                    {
                      "productId": 11,
                      "quantity": 2
                    }
                    """
                )]
            )]
        ),
        responses = [
            ApiResponse(responseCode = "201", description = "추가 성공 (본문 없음)"),
            ApiResponse(
                responseCode = "400",
                description = "검증 오류",
                content = [Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class),
                    examples = [ExampleObject(value = """{ "message": "수량은 1개 이상이어야 합니다." }""")]
                )]
            )
        ]
    )
    @PostMapping("/items")
    fun addItem(
        @Parameter(hidden = true) userId: Long,
        @RequestBody request: AddItemRequest
    )

    /* ───────────────────────────────────── 수량 수정 ───────────────────────────────────── */
    @Operation(
        summary = "장바구니 수량 수정",
        parameters = [Parameter(name = "itemId", description = "CartItem PK", example = "101", required = true)],
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = [Content(
                schema = Schema(implementation = UpdateQuantityRequest::class),
                examples = [ExampleObject(value = """{ "quantity": 3 }""")]
            )]
        ),
        responses = [
            ApiResponse(responseCode = "204", description = "수정 성공 (본문 없음)"),
            ApiResponse(
                responseCode = "400",
                description = "검증/비즈니스 오류",
                content = [Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class),
                    examples = [ExampleObject(value = """{ "message": "장바구니에 없는 상품입니다." }""")]
                )]
            )
        ]
    )
    @PatchMapping("/items/{itemId}")
    fun updateQuantity(
        @Parameter(hidden = true) userId: Long,
        @PathVariable itemId: Long,
        @RequestBody request: UpdateQuantityRequest
    )

    /* ───────────────────────────────────── 아이템 삭제 ───────────────────────────────────── */
    @Operation(
        summary = "장바구니 아이템 삭제",
        parameters = [Parameter(name = "itemId", description = "CartItem PK", example = "101", required = true)],
        responses = [
            ApiResponse(responseCode = "204", description = "삭제 성공 (본문 없음)"),
            ApiResponse(
                responseCode = "404",
                description = "CartItem 을 찾을 수 없음",
                content = [Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class),
                    examples = [ExampleObject(value = """{ "message": "해당 아이템이 존재하지 않습니다." }""")]
                )]
            )
        ]
    )
    @DeleteMapping("/items/{itemId}")
    fun removeItem(
        @Parameter(hidden = true) userId: Long,
        @PathVariable itemId: Long
    )
}
