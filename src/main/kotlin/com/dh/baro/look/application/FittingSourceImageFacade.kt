package com.dh.baro.look.application

import com.dh.baro.look.application.dto.FittingSourceImageUploadInfo
import com.dh.baro.look.domain.FittingSourceImage
import com.dh.baro.look.domain.service.FittingSourceImageService
import com.dh.baro.look.infra.s3.S3ImageClient
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FittingSourceImageFacade(
    private val fittingSourceImageService: FittingSourceImageService,
    private val s3ImageClient: S3ImageClient,
) {

    @Transactional
    fun generateUploadUrl(userId: Long): FittingSourceImageUploadInfo {
        val pendingImage = fittingSourceImageService.createPendingImage(userId)
        val s3Info = s3ImageClient.generatePresignedUrl(pendingImage.id)

        pendingImage.setS3Key(s3Info.s3Key)

        return FittingSourceImageUploadInfo(
            imageId = pendingImage.id,
            presignedUrl = s3Info.presignedUrl,
            expiresAt = s3Info.expiresAt,
            maxFileSize = s3Info.maxFileSize,
            allowedTypes = s3Info.allowedTypes,
        )
    }

    @Transactional
    fun completeImageUpload(imageId: Long, userId: Long) {
        val image = fittingSourceImageService.getFittingSourceImage(imageId, userId)
        val imageUrl = s3ImageClient.getImageUrl(image.getS3Key()!!)
        fittingSourceImageService.completeImageUpload(image, imageUrl)
    }

    @Transactional(readOnly = true)
    fun getUserFittingSourceImages(userId: Long): List<FittingSourceImage> {
        return fittingSourceImageService.getUserFittingSourceImages(userId)
    }
}
