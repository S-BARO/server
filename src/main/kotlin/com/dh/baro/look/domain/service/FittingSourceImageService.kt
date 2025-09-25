package com.dh.baro.look.domain.service

import com.dh.baro.core.ErrorMessage
import com.dh.baro.look.domain.FittingSourceImage
import com.dh.baro.look.domain.FittingSourceImageStatus
import com.dh.baro.look.domain.repository.FittingSourceImageRepository
import org.springframework.stereotype.Service

@Service
class FittingSourceImageService(
    private val fittingSourceImageRepository: FittingSourceImageRepository,
) {

    fun createPendingImage(userId: Long): FittingSourceImage {
        val image = FittingSourceImage.newPendingImage(userId)
        return fittingSourceImageRepository.save(image)
    }

    fun getFittingSourceImage(imageId: Long, userId: Long): FittingSourceImage {
        val image = fittingSourceImageRepository.findById(imageId).orElse(null)
            ?: throw IllegalArgumentException(ErrorMessage.FITTING_SOURCE_IMAGE_NOT_FOUND.format(imageId))
        require(image.userId == userId) { "이미지 소유자가 아닙니다." }
        requireNotNull(image.getS3Key()) { "S3 키가 설정되지 않았습니다." }

        return image
    }

    fun completeImageUpload(image: FittingSourceImage, imageUrl: String) {
        image.markAsUploaded(imageUrl)
    }

    fun getUserFittingSourceImages(userId: Long): List<FittingSourceImage> =
        fittingSourceImageRepository.findByUserIdAndUploadStatusOrderByIdDesc(userId, FittingSourceImageStatus.COMPLETED)
}
