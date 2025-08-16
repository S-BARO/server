package com.dh.baro.product.application.dto

import com.dh.baro.identity.domain.Store
import com.dh.baro.product.domain.Product
import org.springframework.data.domain.Slice

data class ProductSliceBundle(
    val productSlice: Slice<Product>,
    val storeList: List<Store>,
)
