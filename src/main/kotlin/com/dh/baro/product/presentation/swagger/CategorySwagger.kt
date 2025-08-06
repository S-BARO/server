package com.dh.baro.product.presentation.swagger

import com.dh.baro.product.presentation.dto.CategoryCreateRequest
import com.dh.baro.product.presentation.dto.CategoryResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.*


@Tag(
    name = "Category API",
    description = "카테고리(Category) 관련 API입니다."
)
@RequestMapping("/categories")
interface CategorySwagger {

    /* ───────────────────────────── 카테고리 생성 ───────────────────────────── */
    @Operation(
        summary = "카테고리 생성(ADMIN 전용)",
        description = """
            새 카테고리를 등록합니다.
            id 값은 카테고리 관리팀이 발급한 고유 번호를 사용합니다.
            ADMIN 권한이 필요합니다
        """,
        requestBody = RequestBody(
            required = true,
            content = [Content(
                schema = Schema(implementation = CategoryCreateRequest::class),
                examples = [ExampleObject(
                    name = "createCategoryRequest",
                    value = """
                    {
                      "id": 10,
                      "name": "ACCESSORY"
                    }
                    """
                )]
            )]
        ),
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "생성 성공",
                content = [Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = CategoryResponse::class),
                    examples = [ExampleObject(
                        name = "createCategoryResponse",
                        value = """
                        {
                          "id": 10,
                          "name": "ACCESSORY"
                        }
                        """
                    )]
                )]
            ),
        ]
    )
    @PostMapping
    fun createCategory(@RequestBody request: CategoryCreateRequest): CategoryResponse
}
