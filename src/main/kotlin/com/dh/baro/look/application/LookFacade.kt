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
import org.slf4j.LoggerFactory
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
    private val logger = LoggerFactory.getLogger(LookFacade::class.java)

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
        return cachedResponse ?: self.getLookDetailFromDatabase(lookId)
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
            logger.info("Look 상세 데이터 조회 및 캐시 저장 완료 - lookId: {}", lookId)
            response
        } catch (e: Exception) {
            logger.error("Look 상세 데이터 조회 실패 - lookId: {}", lookId, e)
            lookCacheService.cacheEmptyLookDetail(lookId)
            throw e
        }
    }
}
