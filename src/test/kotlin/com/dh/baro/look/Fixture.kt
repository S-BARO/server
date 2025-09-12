package com.dh.baro.look

import com.dh.baro.look.domain.Look

fun lookFixture(
    id: Long,
    title: String,
    imageUrls: List<String>,
    productIds: List<Long>,
): Look {
    val look = Look(
        id = id,
        creatorId = 1L,
        title = title,
        description = null,
        thumbnailUrl = "thumb://$id",
    )
    look.addImages(imageUrls)
    look.addProducts(productIds)
    return look
}
