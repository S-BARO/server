package com.dh.baro.look.application

import com.dh.baro.look.application.dto.AiFittingInfo
import com.dh.baro.look.infra.gemini.GeminiImageClient
import org.springframework.stereotype.Service

@Service
class AiFittingFacade(
    private val geminiImageClient: GeminiImageClient,
) {

    fun generateAiFitting(sourceImageUrl: String, clothingImageUrl: String): AiFittingInfo {
        val generatedImageData = geminiImageClient.generateAiFitting(sourceImageUrl, clothingImageUrl)

        return AiFittingInfo(
            sourceImageUrl = sourceImageUrl,
            clothingImageUrl = clothingImageUrl,
            generatedImageData = generatedImageData,
        )
    }
}
