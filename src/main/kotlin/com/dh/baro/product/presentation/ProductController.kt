package com.dh.baro.product.presentation

import com.dh.baro.product.domain.ProductQueryService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/products")
class ProductController(
    private val productQueryService: ProductQueryService
) {

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    fun getPopularProducts(
        @RequestParam(required = false) categoryId: Long?,
        @RequestParam(required = false) cursorId: Long?,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) size: Int,
    ): List<ProductListItem> =
        productQueryService.getPopularProducts(categoryId, cursorId, size)
            .map(ProductListItem::from)

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
