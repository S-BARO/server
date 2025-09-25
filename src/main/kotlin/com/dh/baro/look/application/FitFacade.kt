package com.dh.baro.look.application

import com.dh.baro.look.application.dto.AiFittingInfo
import com.dh.baro.look.application.dto.FittingSourceImageUploadInfo
import com.dh.baro.look.domain.FittingSourceImage
import com.dh.baro.look.domain.service.CreditService
import com.dh.baro.look.domain.service.FittingSourceImageService
import com.dh.baro.look.infra.gemini.GeminiImageApi
import com.dh.baro.look.infra.s3.S3ImageApi
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FitFacade(
    private val fittingSourceImageService: FittingSourceImageService,
    private val s3ImageApi: S3ImageApi,
    private val geminiImageApi: GeminiImageApi,
    private val creditService: CreditService,
) {

    @Transactional
    fun createUploadUrl(userId: Long): FittingSourceImageUploadInfo {
        val pendingImage = fittingSourceImageService.createPendingImage(userId)
        val s3Info = s3ImageApi.generatePresignedUrl(pendingImage.s3Key)

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
        val imageUrl = s3ImageApi.getImageUrl(image.s3Key)
        fittingSourceImageService.completeImageUpload(image, imageUrl)
    }

    @Transactional(readOnly = true)
    fun getUserFittingSourceImages(userId: Long): List<FittingSourceImage> {
        return fittingSourceImageService.getUserFittingSourceImages(userId)
    }

    fun generateAiFitting(userId: Long, sourceImageUrl: String, clothingImageUrl: String): AiFittingInfo {
        var generatedImageData: ByteArray? = null

        creditService.executeWithCreditCheck(userId) {
            generatedImageData = geminiImageApi.generateAiFitting(sourceImageUrl, clothingImageUrl)
        }

        return AiFittingInfo(
            sourceImageUrl = sourceImageUrl,
            clothingImageUrl = clothingImageUrl,
            generatedImageData = generatedImageData!!,
        )
    }
}
