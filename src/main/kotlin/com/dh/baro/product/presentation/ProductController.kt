package com.dh.baro.product.presentation

import com.dh.baro.core.Cursor
import com.dh.baro.core.ErrorMessage
import com.dh.baro.core.SliceResponse
import com.dh.baro.core.annotation.CheckAuth
import com.dh.baro.core.annotation.CurrentUser
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
    override fun getProductDetail(
        @CurrentUser userId: Long?,
        @PathVariable productId: String,
    ): ProductDetail {
        val productDetailBundle = productFacade.getProductDetail(productId.toLong(), userId)
        return ProductDetail.from(
            productDetailBundle.product,
            productDetailBundle.store,
            productDetailBundle.isLiked
        )
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    override fun getPopularProducts(
        @CurrentUser userId: Long?,
        @RequestParam(required = false) categoryId: Long?,
        @RequestParam(required = false) cursorId: Long?,
        @RequestParam(required = false) cursorLikes: Int?,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) size: Int,
    ): SliceResponse<ProductListItem> {
        if ((cursorId == null) xor (cursorLikes == null))
            throw IllegalArgumentException(ErrorMessage.INVALID_POPULAR_PRODUCT_CURSOR.message)

        val productSliceBundle = productFacade.getPopularProducts(categoryId, cursorLikes, cursorId, size, userId)
        val storeMap = productSliceBundle.storeList.associateBy { it.id }

        return SliceResponse.fromNullable(
            slice = productSliceBundle.productSlice,
            mapper = { p -> ProductListItem.ofOrNull(p, storeMap, productSliceBundle.likedProductIds) },
            cursorExtractor = { PopularCursor(it.id, it.getLikesCount()) },
        )
    }

    @GetMapping("/newest")
    @ResponseStatus(HttpStatus.OK)
    override fun getNewestProducts(
        @CurrentUser userId: Long?,
        @RequestParam(required = false) categoryId: Long?,
        @RequestParam(required = false) cursorId: Long?,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) size: Int,
    ): SliceResponse<ProductListItem> {
        val productSliceBundle = productFacade.getNewestProducts(categoryId, cursorId, size, userId)
        val storeMap = productSliceBundle.storeList.associateBy { it.id }

        return SliceResponse.fromNullable(
            slice = productSliceBundle.productSlice,
            mapper = { p -> ProductListItem.ofOrNull(p, storeMap, productSliceBundle.likedProductIds) },
            cursorExtractor = { Cursor(it.id) },
        )
    }

    @PostMapping("/{productId}/likes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun likeProduct(
        @CurrentUser userId: Long,
        @PathVariable productId: String,
    ) {
        productFacade.likeProduct(userId, productId.toLong())
    }

    @DeleteMapping("/{productId}/likes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun cancelProductLike(
        @CurrentUser userId: Long,
        @PathVariable productId: String,
    ) {
        productFacade.cancelProductLike(userId, productId.toLong())
    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = "21"
    }
}
