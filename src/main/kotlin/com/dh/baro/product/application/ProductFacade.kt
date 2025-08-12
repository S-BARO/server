package com.dh.baro.product.application

import com.dh.baro.product.domain.service.CategoryService
import com.dh.baro.product.domain.Product
import com.dh.baro.product.domain.service.ProductService
import org.springframework.stereotype.Service

@Service
class ProductFacade(
    private val productService: ProductService,
    private val categoryService: CategoryService,
) {

    fun createProduct(cmd: ProductCreateCommand): Product {
        val categories = categoryService.getCategoriesByIds(cmd.categoryIds)
        return productService.createProduct(cmd, categories)
    }
}
