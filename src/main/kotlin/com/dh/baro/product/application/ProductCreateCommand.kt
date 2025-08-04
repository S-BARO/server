package com.dh.baro.product.application

import com.dh.baro.product.presentation.dto.ProductCreateRequest
import java.math.BigDecimal

data class ProductCreateCommand(
    val name: String,
    val price: BigDecimal,
    val quantity: Int,
    val description: String?,
    val likesCount: Int,
    val thumbnailUrl: String,
) {

    companion object {
        fun toCommand(request: ProductCreateRequest): ProductCreateCommand =
            ProductCreateCommand(
                name = request.name,
                price = request.price,
                quantity = request.quantity,
                description = request.description,
                likesCount = request.likesCount,
                thumbnailUrl = request.thumbnailUrl,
            )
    }
}
