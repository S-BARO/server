package com.dh.baro.look.application.dto

import com.dh.baro.identity.domain.Store
import com.dh.baro.look.domain.Look
import com.dh.baro.product.domain.Product

data class LookDetailBundle (
    val look: Look,
    val orderedProductIds: List<Long>,
    val products: List<Product>,
    val stores: List<Store>,
)
