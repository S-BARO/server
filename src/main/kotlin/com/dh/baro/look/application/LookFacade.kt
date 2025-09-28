package com.dh.baro.look.application

import com.dh.baro.identity.domain.service.StoreService
import com.dh.baro.identity.domain.service.UserService
import com.dh.baro.look.application.dto.LookCreateCommand
import com.dh.baro.look.application.dto.LookDetailBundle
import com.dh.baro.look.domain.*
import com.dh.baro.look.domain.service.LookService
import com.dh.baro.look.domain.service.SwipeService
import com.dh.baro.look.infra.redis.LookCacheService
import com.dh.baro.look.presentation.dto.LookDetailResponse
import com.dh.baro.product.domain.service.ProductQueryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LookFacade(
    private val userService: UserService,
    private val storeService: StoreService,
    private val productQueryService: ProductQueryService,
    private val lookService: LookService,
    private val swipingService: SwipeService,
    private val lookCacheService: LookCacheService,
) {

    @Autowired
    @Lazy
    private lateinit var self: LookFacade

    @Transactional
    fun createLook(cmd: LookCreateCommand): Look {
        userService.checkUserExists(cmd.creatorId)
        productQueryService.checkProductsExists(cmd.productIds)
        return lookService.createLook(cmd)
    }

    @Transactional(readOnly = true)
    fun getSwipeLooks(userId: Long, cursorId: Long?, size: Int): Slice<Look> {
        val lookIds = swipingService.getLookIdsByUserId(userId)
        return lookService.getLooksForSwipe(lookIds, cursorId, size)
    }

    fun getLookDetail(lookId: Long): LookDetailResponse {
        val cachedResponse = lookCacheService.getCachedLookDetail(lookId)
        if (cachedResponse != null) {
            return cachedResponse
        }

        return self.getLookDetailFromDatabase(lookId)
    }

    @Transactional(readOnly = true)
    fun getLookDetailFromDatabase(lookId: Long): LookDetailResponse {
        return try {
            val look = lookService.getLookDetail(lookId)

            val orderedProductIds = look.getOrderedProductIds()
            val products = productQueryService.getAllByIds(orderedProductIds)

            val storeIds = products.map { it.storeId }.toSet()
            val stores = storeService.getStoresByIds(storeIds)

            val lookDetailBundle = LookDetailBundle(
                look = look,
                orderedProductIds = orderedProductIds,
                products = products,
                stores = stores,
            )

            val response = LookDetailResponse.from(lookDetailBundle)
            lookCacheService.cacheLookDetail(lookId, response)
            response
        } catch (e: Exception) {
            lookCacheService.cacheEmptyLookDetail(lookId)
            throw e
        }
    }
}
