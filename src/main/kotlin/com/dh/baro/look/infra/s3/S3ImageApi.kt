package com.dh.baro.look.infra.s3

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration
import java.time.Instant
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

@Component
class S3ImageApi(
    @Value("\${cloud.aws.s3.bucket}")
    val bucketName: String,
    @Value("\${cloud.aws.region.static}")
    val region: String,
    @Value("\${cloud.aws.credentials.access-key}")
    val accessKey: String,
    @Value("\${cloud.aws.credentials.secret-key}")
    val secretKey: String,
) {

    private val isolatedExecutor = Executors.newCachedThreadPool()

    fun generatePresignedUrl(s3Key: String): S3PresignedUrlInfo {
        // 별도 스레드에서 실행하여 요청 컨텍스트 격리
        return CompletableFuture.supplyAsync({
            val duration = Duration.ofMinutes(UPLOAD_DURATION_MINUTES)
            val expiresAt = Instant.now().plus(duration)

            val credentials = AwsBasicCredentials.create(accessKey, secretKey)

            val s3Presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build()

            try {
                val putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build()

                val presignRequest = PutObjectPresignRequest.builder()
                    .putObjectRequest(putObjectRequest)
                    .signatureDuration(duration)
                    .build()

                val presignedRequest = s3Presigner.presignPutObject(presignRequest)

                S3PresignedUrlInfo(
                    presignedUrl = presignedRequest.url().toString(),
                    s3Key = s3Key,
                    imageUrl = "https://$bucketName.s3.$region.amazonaws.com/$s3Key",
                    expiresAt = expiresAt,
                    maxFileSize = MAX_FILE_SIZE_BYTES,
                    allowedTypes = ALLOWED_CONTENT_TYPES,
                )
            } finally {
                s3Presigner.close()
            }
        }, isolatedExecutor).get()
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
