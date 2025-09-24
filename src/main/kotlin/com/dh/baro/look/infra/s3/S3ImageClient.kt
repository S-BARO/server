package com.dh.baro.look.infra.s3

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration
import java.time.Instant
import java.util.*

@Component
class S3ImageClient(
    @Value("\${cloud.aws.s3.bucket}")
    val bucketName: String,
    @Value("\${cloud.aws.region.static}")
    val region: String,
) {

    fun generatePresignedUrl(imageId: Long): S3PresignedUrlInfo {
        val s3Key = generateS3Key(imageId)
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

    private fun generateS3Key(imageId: Long): String {
        val timestamp = System.currentTimeMillis()
        val uuid = UUID.randomUUID().toString().substring(0, 8)
        return "fitting-source-images/$imageId/$timestamp-$uuid.jpg"
    }

    companion object {
        const val MAX_FILE_SIZE_BYTES = 10485760L // 10MB
        const val UPLOAD_DURATION_MINUTES = 10L

        val ALLOWED_CONTENT_TYPES = listOf(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp"
        )
    }
}
