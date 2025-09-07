package com.dh.baro.order.presentation.swagger

import com.dh.baro.core.ErrorResponse
import com.dh.baro.core.SliceResponse
import com.dh.baro.order.presentation.dto.OrderCreateRequest
import com.dh.baro.order.presentation.dto.OrderDetailResponse
import com.dh.baro.order.presentation.dto.OrderSummary
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@Tag(
    name = "Order API",
    description = "주문(Order) 관련 API입니다. BUYER 권한이 필요합니다."
)
@RequestMapping("/orders")
interface OrderSwagger {

    /* ────────────────────────────────────────── 주문 생성 ───────────────────────────────────────── */
    @Operation(
        summary = "주문 생성",
        description = """
            장바구니 페이지에서 “주문하기” 버튼을 눌렀을 때 호출되는 API 입니다.  
            서버는 재고를 확인 후 재고가 남아있는 경우 차감합니다.
        """,
        requestBody = RequestBody(
            required = true,
            content = [Content(
                schema = Schema(implementation = OrderCreateRequest::class),
                examples = [ExampleObject(
                    name = "orderCreate",
                    value = """
                    {
                      "shippingAddress": "서울특별시 강남구 테헤란로 123",
                      "orderItems": [
                        { "productId": 11, "quantity": 2 },
                        { "productId": 12, "quantity": 1 }
                      ]
                    }
                    """
                )]
            )]
        ),
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "주문 생성 성공",
                content = [Content(
                    schema = Schema(implementation = OrderDetailResponse::class),
                    examples = [ExampleObject(
                        name = "orderDetail",
                        value = """
                        {
                          "orderId": 1001,
                          "orderStatus": "ORDERED",
                          "shippingAddress": "서울특별시 강남구 테헤란로 123",
                          "totalPrice": 4000,
                          "orderedAt": "2025-05-10T19:10:23.123Z",
                          "items": [
                            {
                              "productId": 11,
                              "productName": "T-Shirt",
                              "thumbnailUrl": "123.jpg",
                              "quantity": 2,
                              "priceAtPurchase": 1000
                            },
                            {
                              "productId": 12,
                              "productName": "Hoodie",
                              "thumbnailUrl": "123.jpg",
                              "quantity": 1,
                              "priceAtPurchase": 2000
                            }
                          ]
                        }
                        """
                    )]
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "검증 오류 / 재고 부족 / 상품 없음",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @PostMapping
    fun placeOrder(
        @Parameter(hidden = true) userId: Long,
        @RequestBody request: OrderCreateRequest
    ): OrderDetailResponse

    /* ────────────────────────────────────────── 주문 상세 ───────────────────────────────────────── */
    @Operation(
        summary = "주문 상세 조회",
        description = "특정 주문에 대한 상세 정보 조회 API 입니다.",
        parameters = [
            Parameter(name = "orderId", description = "주문 PK", required = true, example = "1001")
        ],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = [Content(
                    schema = Schema(implementation = OrderDetailResponse::class),
                    examples = [ExampleObject(
                        name = "orderDetail",
                        value = """
                        {
                          "orderId": 1001,
                          "orderStatus": "ORDERED",
                          "shippingAddress": "서울특별시 강남구 테헤란로 123",
                          "totalPrice": 4000,
                          "orderedAt": "2025-05-10T19:10:23.123Z",
                          "items": [
                            {
                              "productId": 11,
                              "productName": "T-Shirt",
                              "thumbnailUrl": "123.jpg",
                              "quantity": 2,
                              "priceAtPurchase": 1000
                            },
                            {
                              "productId": 12,
                              "productName": "Hoodie",
                              "thumbnailUrl": "123.jpg",
                              "quantity": 1,
                              "priceAtPurchase": 2000
                            }
                          ]
                        }
                        """
                    )]
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "주문을 찾을 수 없음",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    @GetMapping("/{orderId}")
    fun getOrderDetail(
        @Parameter(hidden = true) userId: Long,
        @PathVariable orderId: String
    ): OrderDetailResponse

    /* ────────────────────────────────────────── 주문 목록(무한 스크롤) ───────────────────────────── */
    @Operation(
        summary = "주문 목록 조회",
        description = """
            주문 목록 조회 API 입니다. 무한 스크롤 방식으로 동작합니다.
            cursorId : 이전 페이지의 마지막 요소에서 받은 커서 값 (필수 아님)
            size : 페이지 크기(기본값 10)
        """,
        parameters = [
            Parameter(name = "cursorId", description = "마지막 주문 PK", required = false, example = "1001"),
            Parameter(name = "size", description = "페이지 크기", required = false, example = "10")
        ],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = [Content(
                    schema = Schema(implementation = SliceResponse::class),
                    examples = [ExampleObject(
                        name = "ordersSlice",
                        value = """
                        {
                          "content": [
                            {
                              "orderId": 1003,
                              "totalPrice": 1500,
                              "orderStatus": "ORDERED",
                              "orderedAt": "2025-05-11T09:12:10Z"
                            },
                            {
                              "orderId": 1002,
                              "totalPrice": 2000,
                              "orderStatus": "ORDERED",
                              "orderedAt": "2025-05-10T20:45:00Z"
                            }
                          ],
                          "hasNext": true,
                          "nextCursor": { "id": 1002 }
                        }
                        """
                    )]
                )]
            )
        ]
    )
    @GetMapping
    fun getOrdersByCursor(
        @Parameter(hidden = true) userId: Long,
        @RequestParam(required = false) cursorId: String?,
        @RequestParam(defaultValue = "10") size: Int
    ): SliceResponse<OrderSummary>
}
