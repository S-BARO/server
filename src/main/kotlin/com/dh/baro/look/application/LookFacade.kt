package com.dh.baro.look.application

import com.dh.baro.identity.domain.service.StoreService
import com.dh.baro.identity.domain.service.UserService
import com.dh.baro.look.application.dto.LookCreateCommand
import com.dh.baro.look.application.dto.LookDetailBundle
import com.dh.baro.look.domain.*
import com.dh.baro.look.domain.service.LookService
import com.dh.baro.look.domain.service.SwipeService
import com.dh.baro.product.domain.service.ProductQueryService
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service

@Service
class LookFacade(
    private val userService: UserService,
    private val storeService: StoreService,
    private val productQueryService: ProductQueryService,
    private val lookService: LookService,
    private val swipingService: SwipeService,
) {

    fun createLook(cmd: LookCreateCommand): Look {
        userService.checkUserExists(cmd.creatorId)
        productQueryService.checkProductsExists(cmd.productIds)
        return lookService.createLook(cmd)
    }

    fun getSwipeLooks(userId: Long, cursorId: Long?, size: Int): Slice<Look> {
        val lookIds = swipingService.getLookIdsByUserId(userId)
        return lookService.getLooksForSwipe(lookIds, cursorId, size)
    }

    fun getLookDetail(lookId: Long): LookDetailBundle {
        val look = lookService.getLookDetail(lookId)

        val orderedProductIds = look.getOrderedProductIds()
        val products = productQueryService.getAllByIds(orderedProductIds)

        val storeIds = products.map { it.storeId }.toSet()
        val stores = storeService.getStoresByIds(storeIds)

        return LookDetailBundle(
            look = look,
            orderedProductIds = orderedProductIds,
            products = products,
            stores = stores,
        )
    }
}
