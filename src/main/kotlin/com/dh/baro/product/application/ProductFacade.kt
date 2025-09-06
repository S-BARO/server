package com.dh.baro.product.application

import com.dh.baro.identity.domain.service.StoreService
import com.dh.baro.product.application.dto.ProductDetailBundle
import com.dh.baro.product.application.dto.ProductSliceBundle
import com.dh.baro.product.domain.service.CategoryService
import com.dh.baro.product.domain.Product
import com.dh.baro.product.domain.service.ProductQueryService
import com.dh.baro.product.domain.service.ProductService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductFacade(
    private val storeService: StoreService,
    private val productService: ProductService,
    private val productQueryService: ProductQueryService,
    private val categoryService: CategoryService,
) {

    fun createProduct(cmd: ProductCreateCommand): Product {
        val categories = categoryService.getCategoriesByIds(cmd.categoryIds)
        return productService.createProduct(cmd, categories)
    }

    fun getProductDetail(productId: Long): ProductDetailBundle {
        val product = productQueryService.getProductDetail(productId)
        val store = storeService.getStoreById(product.storeId)
        return ProductDetailBundle(product, store)
    }

    @Transactional(readOnly = true)
    fun getPopularProducts(
        categoryId: Long?,
        cursorLikes: Int?,
        cursorId: Long?,
        size: Int,
    ): ProductSliceBundle {
        val productSlice = productQueryService.getPopularProducts(categoryId, cursorLikes, cursorId, size)
        val stores = storeService.getStoresByIds(productSlice.content.map { it.storeId }.toSet())
        return ProductSliceBundle(productSlice, stores)
    }

    fun getNewestProducts(
        categoryId: Long?,
        cursorId: Long?,
        size: Int,
    ): ProductSliceBundle {
        val productSlice = productQueryService.getNewestProducts(categoryId, cursorId, size)
        val stores = storeService.getStoresByIds(productSlice.content.map { it.storeId }.toSet())
        return ProductSliceBundle(productSlice, stores)
    }
}
