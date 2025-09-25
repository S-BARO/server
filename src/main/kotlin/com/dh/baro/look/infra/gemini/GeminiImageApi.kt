package com.dh.baro.look.infra.gemini

import com.dh.baro.core.ErrorMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.util.*

@Component
class GeminiImageApi(
    @Value("\${gemini.api.key}") private val geminiApiKey: String,
    @Value("\${gemini.api.url}") private val geminiApiUrl: String,
) {

    private val restClient = RestClient.create(geminiApiUrl)

    fun generateAiFitting(sourceImageUrl: String, clothingImageUrl: String): ByteArray {
        val sourceImageData = downloadAndEncode(sourceImageUrl)
        val clothingImageData = downloadAndEncode(clothingImageUrl)

        val request = buildGeminiRequest(sourceImageData, clothingImageData)
        val response = callGeminiApi(request)

        return extractImageData(response)
    }

    private fun downloadAndEncode(imageUrl: String): String {
        return try {
            val imageBytes = RestClient.create()
                .get()
                .uri(imageUrl)
                .retrieve()
                .body(ByteArray::class.java)
                ?: throw IllegalStateException(ErrorMessage.IMAGE_DOWNLOAD_NO_DATA.message)

            Base64.getEncoder().encodeToString(imageBytes)
        } catch (e: Exception) {
            throw IllegalArgumentException(ErrorMessage.IMAGE_DOWNLOAD_FAILED.format(e.message ?: "Unknown error"))
        }
    }

    private fun buildGeminiRequest(sourceImageData: String, clothingImageData: String): GeminiApiRequest {
        val prompt = """
            Create a professional e-commerce fashion photo.
            Take the clothing item from the first image and apply it to the person in the second image.
            Generate a realistic, full-body shot with proper lighting and shadows.
        """.trimIndent()

        return GeminiApiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(
                        GeminiImagePart(GeminiInlineData("image/jpeg", clothingImageData)),
                        GeminiImagePart(GeminiInlineData("image/jpeg", sourceImageData)),
                        GeminiTextPart(prompt)
                    )
                )
            )
        )
    }

    private fun callGeminiApi(request: GeminiApiRequest): GeminiApiResponse {
        return try {
            restClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .header("x-goog-api-key", geminiApiKey)
                .body(request)
                .retrieve()
                .body(GeminiApiResponse::class.java)
                ?: throw IllegalStateException(ErrorMessage.GEMINI_API_REQUEST_FAILED.format("No response body"))
        } catch (e: Exception) {
            throw IllegalArgumentException(ErrorMessage.GEMINI_API_REQUEST_FAILED.format(e.message ?: "Unknown error"))
        }
    }

    private fun extractImageData(response: GeminiApiResponse): ByteArray {
        val candidate = response.candidates?.firstOrNull()
            ?: throw IllegalStateException(ErrorMessage.GEMINI_API_NO_CANDIDATES.message)

        val imagePart = candidate.content?.parts?.filterIsInstance<GeminiImagePart>()?.firstOrNull()
            ?: throw IllegalStateException(ErrorMessage.GEMINI_API_NO_IMAGE_DATA.message)

        return Base64.getDecoder().decode(imagePart.inlineData.data)
    }
}
