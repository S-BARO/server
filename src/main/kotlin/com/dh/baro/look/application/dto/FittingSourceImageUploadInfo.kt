package com.dh.baro.look.application.dto

import java.time.Instant

data class FittingSourceImageUploadInfo(
    val imageId: Long,
    val presignedUrl: String,
    val expiresAt: Instant,
    val maxFileSize: Long,
    val allowedTypes: List<String>,
)
