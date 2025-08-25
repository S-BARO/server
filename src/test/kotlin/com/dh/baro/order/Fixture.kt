package com.dh.baro.order

import com.dh.baro.product.domain.*
import java.math.BigDecimal

fun productFixture(
    id: Long,
    name: String,
    price: BigDecimal = BigDecimal("1000"),
    quantity: Int = 10,
): Product =
    Product(
        id = id,
        name = name,
        storeId = 123L,
        price = price,
        quantity = quantity,
        thumbnailUrl = "https://example.com/$id-thumb.jpg",
        likesCount = 0,
    ).apply {
        this.addImages(listOf("https://example.com/$id-1.jpg"))
    }
