package com.dh.baro.product.presentation.dto

import com.dh.baro.product.application.ProductCreateCommand
import jakarta.validation.constraints.*
import java.math.BigDecimal

data class ProductCreateRequest(
    @field:Size(min = 1, max = 100)
    val name: String,

    @field:NotNull
    val storeId: Long,

    @field:Positive
    val price: BigDecimal,

    @field:PositiveOrZero
    val quantity: Int,

    val description: String? = null,

    @field:PositiveOrZero
    val likesCount: Int,

    @field:NotBlank
    val thumbnailUrl: String,

    @field:NotEmpty
    val categoryIds: List<Long>,

    @field:Size(min = 1, message = "최소 1개 이상의 이미지가 필요합니다.")
    val imageUrls: List<@NotBlank String>,
) {
    fun toCommand(): ProductCreateCommand =
        ProductCreateCommand(
            name = name,
            storeId = storeId,
            price = price,
            quantity = quantity,
            description = description,
            likesCount = likesCount,
            thumbnailUrl = thumbnailUrl,
            categoryIds = categoryIds,
            imageUrls = imageUrls,
        )
}
