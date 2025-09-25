package com.dh.baro.look.infra.s3

import java.time.Instant

data class S3PresignedUrlInfo(
    val presignedUrl: String,
    val s3Key: String,
    val imageUrl: String,
    val expiresAt: Instant,
    val maxFileSize: Long,
    val allowedTypes: List<String>,
)
