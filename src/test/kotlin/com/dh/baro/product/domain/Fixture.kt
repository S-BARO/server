package com.dh.baro.product.domain

import java.math.BigDecimal
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.random.Random

fun categoryFixture(id: Long, name: String = "CAT_$id") =
    Category(id = id, name = name)

fun productFixture(
    id: Long,
    name: String,
    category: Category,
    likes: Int = Random.nextInt(0, 500),
    createdAtAgoDays: Long = 0L // 0=지금, 31=한달 전
): Product {
    val product = Product(
        id = id,
        name = name,
        price = BigDecimal("19900"),
        quantity = 10,
        thumbnailUrl = "https://example.com/$id-thumb.jpg",
        likesCount = likes,
    )

    product.images += ProductImage(
        id = id * 100,
        product = product,
        imageUrl = "https://example.com/$id-1.jpg",
        displayOrder = 1,
    )

    product.productCategories += ProductCategory(
        id = id * 10,
        product = product,
        category = category,
    )

    if (createdAtAgoDays > 0) {
        val field = product.javaClass.superclass!!.getDeclaredField("createdAt")
        field.isAccessible = true
        field.set(
            product,
            Instant.now().minus(createdAtAgoDays, ChronoUnit.DAYS),
        )
    }
    return product
}
