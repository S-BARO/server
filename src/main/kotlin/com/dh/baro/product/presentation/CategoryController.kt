package com.dh.baro.product.presentation

import com.dh.baro.core.auth.CheckAuth
import com.dh.baro.identity.domain.UserRole
import com.dh.baro.product.application.CategoryFacade
import com.dh.baro.product.presentation.dto.CategoryCreateRequest
import com.dh.baro.product.presentation.dto.CategoryResponse
import com.dh.baro.product.presentation.swagger.CategorySwagger
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/categories")
class CategoryController(
    private val categoryFacade: CategoryFacade,
) : CategorySwagger {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CheckAuth(UserRole.ADMIN)
    override fun createCategory(@Valid @RequestBody request: CategoryCreateRequest): CategoryResponse {
        val created = categoryFacade.createCategory(request)
        return CategoryResponse.from(created)
    }
}
