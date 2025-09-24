package com.dh.baro.look.presentation.dto

import com.dh.baro.look.domain.FittingSourceImage
import com.dh.baro.look.domain.FittingSourceImageStatus
import java.time.Instant

data class FittingSourceImageDto(
    val id: Long,
    val imageUrl: String?,
    val uploadStatus: FittingSourceImageStatus,
    val createdAt: Instant?,
) {
    companion object {
        fun from(image: FittingSourceImage): FittingSourceImageDto =
            FittingSourceImageDto(
                id = image.id,
                imageUrl = image.getImageUrl(),
                uploadStatus = image.getUploadStatus(),
                createdAt = image.createdAt,
            )
    }
}
