package com.dh.baro.look.presentation.dto

import com.dh.baro.look.application.FittingSourceImageUploadInfo
import java.time.Instant

data class FittingSourceImageUploadUrlResponse(
    val imageId: Long,
    val presignedUrl: String,
    val expiresAt: Instant,
    val maxFileSize: Long,
    val allowedTypes: List<String>,
) {
    companion object {
        fun from(info: FittingSourceImageUploadInfo): FittingSourceImageUploadUrlResponse =
            FittingSourceImageUploadUrlResponse(
                imageId = info.imageId,
                presignedUrl = info.presignedUrl,
                expiresAt = info.expiresAt,
                maxFileSize = info.maxFileSize,
                allowedTypes = info.allowedTypes,
            )
    }
}
