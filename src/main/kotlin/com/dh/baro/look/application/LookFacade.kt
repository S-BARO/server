package com.dh.baro.look.application

import com.dh.baro.identity.domain.service.UserService
import com.dh.baro.look.domain.*
import com.dh.baro.look.domain.service.LookImageService
import com.dh.baro.look.domain.service.LookProductService
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
    private val lookImageService: LookImageService,
    private val lookProductService: LookProductService,
) {

    fun createLook(cmd: LookCreateCommand): Look {
        userService.checkUserExists(cmd.creatorId)
        productQueryService.checkProductsExists(cmd.productIds)
        return lookService.createLook(cmd)
    }

    fun getSwipeLooks(userId: Long, cursorId: Long?, size: Int): Slice<Look> =
        lookService.getSwipeLooks(userId, cursorId, size)

    fun getLookDetail(lookId: Long): LookDetailResponse {
        val look = lookService.getLook(lookId)
        val images = lookImageService.findByLookIdOrderByDisplay(lookId)
        val lookProducts = lookProductService.findByLookIdOrderByDisplay(lookId)

        val productIds = lookProducts.map { it.productId }.distinct()
        val products = productQueryService.getAllByIds(productIds)

        return LookDetailResponse.of(
            look = look,
            images = images,
            lookProducts = lookProducts,
            products = products,
        )
    }
}
