package com.dh.baro.product.application

import com.dh.baro.product.domain.Category
import com.dh.baro.product.domain.service.CategoryService
import com.dh.baro.product.presentation.dto.CategoryCreateRequest
import org.springframework.stereotype.Service

@Service
class CategoryFacade(
    private val categoryService: CategoryService,
) {

    fun createCategory(request: CategoryCreateRequest): Category =
        categoryService.createCategory(request.id.toLong(), request.name)
}
