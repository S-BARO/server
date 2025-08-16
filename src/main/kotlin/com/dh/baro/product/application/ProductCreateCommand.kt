package com.dh.baro.product.application

import java.math.BigDecimal

data class ProductCreateCommand(
    val name: String,
    val storeId: Long,
    val price: BigDecimal,
    val quantity: Int,
    val description: String?,
    val likesCount: Int,
    val thumbnailUrl: String,
    val categoryIds: List<Long>,
    val imageUrls: List<String>,
)
