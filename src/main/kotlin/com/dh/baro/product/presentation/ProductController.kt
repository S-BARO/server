package com.dh.baro.product.presentation

import com.dh.baro.core.Cursor
import com.dh.baro.core.ErrorMessage
import com.dh.baro.core.SliceResponse
import com.dh.baro.core.auth.CheckAuth
import com.dh.baro.identity.domain.UserRole
import com.dh.baro.product.application.ProductFacade
import com.dh.baro.product.domain.service.ProductQueryService
import com.dh.baro.product.presentation.dto.*
import com.dh.baro.product.presentation.swagger.ProductSwagger
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/products")
class ProductController(
    private val productFacade: ProductFacade,
    private val productQueryService: ProductQueryService,
) : ProductSwagger {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CheckAuth(UserRole.STORE_OWNER)
    override fun createProduct(@Valid @RequestBody request: ProductCreateRequest): ProductCreateResponse =
        ProductCreateResponse.from(productFacade.createProduct(request.toCommand()))

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    override fun getPopularProducts(
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
            cursorExtractor = { PopularCursor(it.id, it.getLikesCount()) },
        )
    }

    @GetMapping("/newest")
    @ResponseStatus(HttpStatus.OK)
    override fun getNewestProducts(
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
    override fun getProductDetail(@PathVariable productId: Long): ProductDetail =
        ProductDetail.from(productQueryService.getProductDetail(productId))

    companion object {
        private const val DEFAULT_PAGE_SIZE = "21"
    }
}
