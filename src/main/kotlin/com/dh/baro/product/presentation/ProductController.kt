package com.dh.baro.product.presentation

import com.dh.baro.core.Cursor
import com.dh.baro.core.ErrorMessage
import com.dh.baro.core.SliceResponse
import com.dh.baro.core.auth.RequireAuth
import com.dh.baro.identity.domain.UserRole
import com.dh.baro.product.application.ProductFacade
import com.dh.baro.product.domain.ProductQueryService
import com.dh.baro.product.presentation.dto.*
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
    @RequireAuth(UserRole.STORE_OWNER)
    fun createProduct(@Valid @RequestBody request: ProductCreateRequest): ProductResponse =
        ProductResponse.from(productFacade.createProduct(request))

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    fun getPopularProducts(
        @RequestParam(required = false) categoryId: Long?,
        @RequestParam(required = false) cursorId: Long?,
        @RequestParam(required = false) cursorLikes: Int?,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) size: Int,
    ): SliceResponse<ProductListItem> {
        if ((cursorId == null) xor (cursorLikes == null))
            throw IllegalArgumentException(ErrorMessage.INVALID_POPULAR_PRODUCT_CURSOR.message)

        val slice = productQueryService.getPopularProducts(categoryId, cursorLikes, cursorId, size)
        return SliceResponse.from(
            slice = slice,
            mapper = ProductListItem::from,
            cursorExtractor = { PopularCursor(it.id, it.likesCount) },
        )
    }

    @GetMapping("/newest")
    @ResponseStatus(HttpStatus.OK)
    fun getNewestProducts(
        @RequestParam(required = false) categoryId: Long?,
        @RequestParam(required = false) cursorId: Long?,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) size: Int,
    ): SliceResponse<ProductListItem> {
        val slice = productQueryService.getNewestProducts(categoryId, cursorId, size)
        return SliceResponse.from(
            slice = slice,
            mapper = ProductListItem::from,
            cursorExtractor = { Cursor(it.id) },
        )
    }

    @GetMapping("/{productId}")
    fun getProductDetail(@PathVariable productId: Long): ProductDetail =
        ProductDetail.from(productQueryService.getProductDetail(productId))

    companion object {
        private const val DEFAULT_PAGE_SIZE = "21"
    }
}
