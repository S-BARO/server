package com.dh.baro.look.domain

import com.dh.baro.core.BaseTimeEntity
import com.dh.baro.core.IdGenerator
import jakarta.persistence.*
import java.util.*

@Entity
@Table(
    name = "fitting_source_images",
    indexes = [
        Index(name = "idx_user_id", columnList = "user_id")
    ]
)
class FittingSourceImage(
    @Id
    @Column(name = "id")
    val id: Long,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "s3_key", nullable = false, length = 500)
    val s3Key: String,

    @Column(name = "image_url", nullable = true, length = 500)
    private var imageUrl: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "upload_status", nullable = false)
    private var uploadStatus: FittingSourceImageStatus = FittingSourceImageStatus.PENDING,
) : BaseTimeEntity() {

    override fun getId(): Long = id

    fun getImageUrl() = imageUrl

    fun getUploadStatus() = uploadStatus

    fun markAsUploaded(imageUrl: String) {
        this.imageUrl = imageUrl
        this.uploadStatus = FittingSourceImageStatus.COMPLETED
    }

    companion object {
        fun newPendingImage(userId: Long): FittingSourceImage {
            val newId = IdGenerator.generate()
            return FittingSourceImage(
                id = newId,
                userId = userId,
                s3Key = generateS3Key(newId),
            )
        }

        private fun generateS3Key(imageId: Long): String {
            val timestamp = System.currentTimeMillis()
            val uuid = UUID.randomUUID().toString().substring(0, 8)
            return "fitting-source-images/$imageId/$timestamp-$uuid.jpg"
        }
    }
}
