package com.dh.baro.product.presentation

import com.dh.baro.core.ErrorMessage
import com.dh.baro.product.application.ProductFacade
import com.dh.baro.product.domain.ProductQueryService
import com.dh.baro.product.presentation.dto.ProductCreateRequest
import com.dh.baro.product.presentation.dto.ProductDetail
import com.dh.baro.product.presentation.dto.ProductListItem
import com.dh.baro.product.presentation.dto.ProductResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/products")
class ProductController(
    private val productFacade: ProductFacade,
    private val productQueryService: ProductQueryService,
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createProduct(@Valid @RequestBody request: ProductCreateRequest): ProductResponse =
        ProductResponse.from(productFacade.createProduct(request))

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    fun getPopularProducts(
        @RequestParam(required = false) categoryId: Long?,
        @RequestParam(required = false) cursorLikes: Int?,
        @RequestParam(required = false) cursorId: Long?,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) size: Int,
    ): List<ProductListItem> {
        if ((cursorLikes == null) xor (cursorId == null))
            throw IllegalArgumentException(ErrorMessage.INVALID_POPULAR_PRODUCT_CURSOR.message)

        return productQueryService.getPopularProducts(categoryId, cursorLikes, cursorId, size)
            .map(ProductListItem::from)
    }

    @GetMapping("/newest")
    @ResponseStatus(HttpStatus.OK)
    fun getNewestProducts(
        @RequestParam(required = false) categoryId: Long?,
        @RequestParam(required = false) cursorId: Long?,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) size: Int,
    ): List<ProductListItem> =
        productQueryService.getNewestProducts(categoryId, cursorId, size)
            .map(ProductListItem::from)

    @GetMapping("/{productId}")
    fun getProductDetail(@PathVariable productId: Long): ProductDetail =
        ProductDetail.from(productQueryService.getProductDetail(productId))

    companion object {
        private const val DEFAULT_PAGE_SIZE = "21"
    }
}
