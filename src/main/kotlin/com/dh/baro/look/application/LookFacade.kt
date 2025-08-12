package com.dh.baro.look.application

import com.dh.baro.identity.domain.service.UserService
import com.dh.baro.look.domain.*
import com.dh.baro.look.domain.service.LookService
import com.dh.baro.look.presentation.dto.LookDetailResponse
import com.dh.baro.product.domain.service.ProductQueryService
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service

@Service
class LookFacade(
    private val userService: UserService,
    private val productQueryService: ProductQueryService,
    private val lookService: LookService,
) {

    fun createLook(cmd: LookCreateCommand): Look {
        userService.checkUserExists(cmd.creatorId)
        productQueryService.checkProductsExists(cmd.productIds)
        return lookService.createLook(cmd)
    }

    fun getSwipeLooks(userId: Long, cursorId: Long?, size: Int): Slice<Look> =
        lookService.getSwipeLooks(userId, cursorId, size)

    fun getLookDetail(lookId: Long): LookDetailResponse {
        val look = lookService.getLookDetail(lookId)
        val orderedProducts = look.getOrderedProducts()

        val productIds = orderedProducts.map { it.productId }.distinct()
        val products = productQueryService.getAllByIds(productIds)

        return LookDetailResponse.of(
            look = look,
            images = look.getOrderedImages(),
            lookProducts = orderedProducts,
            products = products,
        )
    }
}
