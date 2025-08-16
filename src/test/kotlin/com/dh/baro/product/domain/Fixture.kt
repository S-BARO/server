package com.dh.baro.product.domain

import java.math.BigDecimal
import java.time.Instant
import java.time.temporal.ChronoUnit

fun categoryFixture(id: Long, name: String) =
    Category(id = id, name = name)

fun productFixture(
    id: Long,
    name: String,
    category: Category,
    likes: Int = 0,
    createdAtAgoDays: Long = 0L // 0=지금, 31=한달 전
): Product {
    val product = Product(
        id = id,
        name = name,
        storeId = 123L,
        price = BigDecimal("19900"),
        quantity = 10,
        thumbnailUrl = "https://example.com/$id-thumb.jpg",
        likesCount = likes,
    )
    product.addImages(listOf("https://example.com/$id-1.jpg"))
    product.addCategory(category)

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
