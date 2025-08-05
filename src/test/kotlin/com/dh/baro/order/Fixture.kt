package com.dh.baro.order

import com.dh.baro.product.domain.*
import java.math.BigDecimal

fun categoryFixture(id: Long, name: String) =
    Category(id = id, name = name)

fun productFixture(
    id: Long,
    name: String,
    category: Category,
    price: BigDecimal = BigDecimal("1000"),
    quantity: Int = 10,
): Product =
    Product(
        id = id,
        name = name,
        price = price,
        quantity = quantity,
        thumbnailUrl = "https://example.com/$id-thumb.jpg",
        likesCount = 0,
    ).apply {
        images += ProductImage(
            id = id * 100,
            product = this,
            imageUrl = "https://example.com/$id-1.jpg",
            displayOrder = 1,
        )
        productCategories += ProductCategory(
            id = id * 10 + 1,
            product = this,
            category = category,
        )
    }
