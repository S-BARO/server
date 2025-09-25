package com.dh.baro.look.infra.s3

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration
import java.time.Instant

@Component
class S3ImageApi(
    @Value("\${cloud.aws.s3.bucket}")
    val bucketName: String,
    @Value("\${cloud.aws.region.static}")
    val region: String,
) {

    fun generatePresignedUrl(s3Key: String): S3PresignedUrlInfo {
        val duration = Duration.ofMinutes(UPLOAD_DURATION_MINUTES)
        val expiresAt = Instant.now().plus(duration)

        val s3Presigner = S3Presigner.builder()
            .region(Region.of(region))
            .build()

        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(s3Key)
            .build()

        val presignRequest = PutObjectPresignRequest.builder()
            .putObjectRequest(putObjectRequest)
            .signatureDuration(duration)
            .build()

        val presignedRequest = s3Presigner.presignPutObject(presignRequest)

        s3Presigner.close()

        return S3PresignedUrlInfo(
            presignedUrl = presignedRequest.url().toString(),
            s3Key = s3Key,
            imageUrl = "https://$bucketName.s3.$region.amazonaws.com/$s3Key",
            expiresAt = expiresAt,
            maxFileSize = MAX_FILE_SIZE_BYTES,
            allowedTypes = ALLOWED_CONTENT_TYPES,
        )
    }

    fun getImageUrl(s3Key: String): String {
        return "https://${bucketName}.s3.${region}.amazonaws.com/${s3Key}"
    }

    companion object {
        const val MAX_FILE_SIZE_BYTES = 10485760L // 10MB
        const val UPLOAD_DURATION_MINUTES = 10L

        val ALLOWED_CONTENT_TYPES = listOf(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp",
        )
    }
}
