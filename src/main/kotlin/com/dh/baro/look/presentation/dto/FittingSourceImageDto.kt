package com.dh.baro.look.presentation.dto

import com.dh.baro.look.domain.FittingSourceImage
import java.time.Instant

data class FittingSourceImageDto(
    val id: Long,
    val imageUrl: String?,
    val createdAt: Instant?,
) {
    companion object {
        fun from(image: FittingSourceImage): FittingSourceImageDto =
            FittingSourceImageDto(
                id = image.id,
                imageUrl = image.getImageUrl(),
                createdAt = image.createdAt,
            )
    }
}
