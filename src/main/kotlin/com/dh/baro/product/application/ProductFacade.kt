package com.dh.baro.product.application

import com.dh.baro.product.domain.CategoryService
import com.dh.baro.product.domain.Product
import com.dh.baro.product.domain.ProductService
import com.dh.baro.product.presentation.dto.ProductCreateRequest
import org.springframework.stereotype.Service

@Service
class ProductFacade(
    private val productService: ProductService,
    private val categoryService: CategoryService,
) {

    fun createProduct(request: ProductCreateRequest): Product {
        val categories = categoryService.getCategoriesByIds(request.categoryIds)
        val cmd = ProductCreateCommand.toCommand(request)
        return productService.createProduct(cmd, categories)
    }
}
