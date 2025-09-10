package com.dh.baro.product.presentation

import com.dh.baro.core.Cursor
import com.dh.baro.core.ErrorMessage
import com.dh.baro.core.SliceResponse
import com.dh.baro.core.annotation.CheckAuth
import com.dh.baro.identity.domain.UserRole
import com.dh.baro.product.application.ProductFacade
import com.dh.baro.product.presentation.dto.*
import com.dh.baro.product.presentation.swagger.ProductSwagger
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/products")
class ProductController(
    private val productFacade: ProductFacade,
) : ProductSwagger {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CheckAuth(UserRole.STORE_OWNER)
    override fun createProduct(
        @Valid @RequestBody request: ProductCreateRequest,
    ): ProductCreateResponse =
        ProductCreateResponse.from(productFacade.createProduct(request.toCommand()))

    @GetMapping("/{productId}")
    override fun getProductDetail(@PathVariable productId: Long): ProductDetail {
        val productDetailBundle = productFacade.getProductDetail(productId)
        return ProductDetail.from(productDetailBundle.product, productDetailBundle.store)
    }

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

        val productSliceBundle = productFacade.getPopularProducts(categoryId, cursorLikes, cursorId, size)
        val storeMap = productSliceBundle.storeList.associateBy { it.id }

        return SliceResponse.fromNullable(
            slice = productSliceBundle.productSlice,
            mapper = { p -> ProductListItem.ofOrNull(p, storeMap) },
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
        val productSliceBundle = productFacade.getNewestProducts(categoryId, cursorId, size)
        val storeMap = productSliceBundle.storeList.associateBy { it.id }

        return SliceResponse.fromNullable(
            slice = productSliceBundle.productSlice,
            mapper = { p -> ProductListItem.ofOrNull(p, storeMap) },
            cursorExtractor = { Cursor(it.id) },
        )
    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = "21"
    }
}
