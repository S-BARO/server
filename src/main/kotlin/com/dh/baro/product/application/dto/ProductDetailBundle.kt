package com.dh.baro.product.application.dto

import com.dh.baro.identity.domain.Store
import com.dh.baro.product.domain.Product

data class ProductDetailBundle (
    val product: Product,
    val store: Store,
)
