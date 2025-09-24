package com.dh.baro.look.domain

import com.dh.baro.core.BaseTimeEntity
import com.dh.baro.core.IdGenerator
import jakarta.persistence.*

@Entity
@Table(name = "fitting_source_images")
class FittingSourceImage(
    @Id
    @Column(name = "id")
    val id: Long,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "s3_key", nullable = false, length = 500)
    private var s3Key: String? = null,

    @Column(name = "image_url", nullable = true, length = 500)
    private var imageUrl: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "upload_status", nullable = false)
    private var uploadStatus: FittingSourceImageStatus = FittingSourceImageStatus.PENDING,
) : BaseTimeEntity() {

    override fun getId(): Long = id

    fun getS3Key() = s3Key

    fun getImageUrl() = imageUrl

    fun getUploadStatus() = uploadStatus

    fun markAsUploaded(imageUrl: String) {
        this.imageUrl = imageUrl
        this.uploadStatus = FittingSourceImageStatus.COMPLETED
    }

    fun setS3Key(s3Key: String) {
        this.s3Key = s3Key
    }

    companion object {
        fun newPendingImage(userId: Long): FittingSourceImage =
            FittingSourceImage(
                id = IdGenerator.generate(),
                userId = userId,
            )
    }
}
