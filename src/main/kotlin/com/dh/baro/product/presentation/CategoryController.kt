package com.dh.baro.product.presentation

import com.dh.baro.product.application.CategoryFacade
import com.dh.baro.product.presentation.dto.CategoryCreateRequest
import com.dh.baro.product.presentation.dto.CategoryResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/categories")
class CategoryController(
    private val categoryFacade: CategoryFacade,
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createCategory(@Valid @RequestBody request: CategoryCreateRequest): CategoryResponse {
        val created = categoryFacade.createCategory(request)
        return CategoryResponse.from(created)
    }
}
