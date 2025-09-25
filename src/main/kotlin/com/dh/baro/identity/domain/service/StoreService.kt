package com.dh.baro.identity.domain.service

import com.dh.baro.core.ErrorMessage
import com.dh.baro.identity.domain.Store
import com.dh.baro.identity.domain.repository.StoreRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class StoreService(
    private val storeRepository: StoreRepository,
) {

    fun getStoresByIds(storeIds: Collection<Long>): List<Store> {
        if (storeIds.isEmpty()) return emptyList()
        return storeRepository.findAllById(storeIds)
    }

    fun getStoreById(storeId: Long): Store =
        storeRepository.findByIdOrNull(storeId)
            ?: throw IllegalArgumentException(ErrorMessage.STORE_NOT_FOUND.format(storeId))
}
