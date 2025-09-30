package com.dh.baro.look.infra.gemini

import com.dh.baro.core.ErrorMessage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.util.*
import kotlin.math.min

@Component
class GeminiImageApi(
    @Value("\${gemini.api.key}") private val geminiApiKey: String,
    @Value("\${gemini.api.url}") private val geminiApiUrl: String,
) {

    private val restClient = RestClient.create(geminiApiUrl)
    private val logger = LoggerFactory.getLogger(GeminiImageApi::class.java)

    fun generateAiFitting(sourceImageUrl: String, clothingImageUrl: String): ByteArray {
        val (sourceImageData, sourceMimeType) = downloadAndEncode(sourceImageUrl)
        val (clothingImageData, clothingMimeType) = downloadAndEncode(clothingImageUrl)

        val request = buildGeminiRequest(
            sourceImageData, sourceMimeType,
            clothingImageData, clothingMimeType
        )
        val response = callGeminiApi(request)

        return extractImageData(response)
    }

    private fun downloadAndEncode(imageUrl: String): Pair<String, String> {
        return try {

            val imageBytes = restClient
                .get()
                .uri(imageUrl)
                .retrieve()
                .body(ByteArray::class.java)
                ?: throw IllegalStateException(ErrorMessage.IMAGE_DOWNLOAD_NO_DATA.message)

            val sizeInMB = imageBytes.size / 1024.0 / 1024.0
            logger.info("Downloaded image size: ${"%.2f".format(sizeInMB)}MB (${imageBytes.size} bytes)")

            if (imageBytes.size < 1000) {
                val preview = String(imageBytes.take(min(200, imageBytes.size)).toByteArray())
                logger.error("Downloaded data too small. Preview: $preview")
                throw IllegalArgumentException("이미지 다운로드 실패 (${imageBytes.size} bytes). Hotlink protection이 활성화되어 있을 수 있습니다.")
            }

            val base64 = Base64.getEncoder().encodeToString(imageBytes)
            logger.info("Base64 encoded size: ${base64.length} chars")

            val mimeType = getMimeTypeFromUrl(imageUrl)
            logger.info("Detected MIME type: $mimeType")

            Pair(base64, mimeType)
        } catch (e: Exception) {
            logger.error("Image download failed for $imageUrl", e)
            throw IllegalArgumentException(ErrorMessage.IMAGE_DOWNLOAD_FAILED.format(e.message ?: "Unknown error"))
        }
    }

    private fun getMimeTypeFromUrl(url: String): String {
        return when {
            url.endsWith(".png", ignoreCase = true) -> "image/png"
            url.endsWith(".jpg", ignoreCase = true) -> "image/jpeg"
            url.endsWith(".jpeg", ignoreCase = true) -> "image/jpeg"
            url.endsWith(".webp", ignoreCase = true) -> "image/webp"
            url.endsWith(".gif", ignoreCase = true) -> "image/gif"
            else -> {
                logger.warn("Unknown image format from URL: $url, defaulting to image/jpeg")
                "image/jpeg"
            }
        }
    }

    private fun buildGeminiRequest(
        sourceImageData: String,
        sourceMimeType: String,
        clothingImageData: String,
        clothingMimeType: String
    ): GeminiApiRequest {
        val prompt = """
            Generate a banana image and return it as your response.
        """.trimIndent()

        return GeminiApiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(
                        GeminiImagePart(GeminiInlineData(clothingMimeType, clothingImageData)),
                        GeminiImagePart(GeminiInlineData(sourceMimeType, sourceImageData)),
                        GeminiTextPart(prompt)
                    )
                )
            )
        )
    }

    private fun callGeminiApi(request: GeminiApiRequest): GeminiApiResponse {
        return try {
            logger.info("Calling Gemini API...")

            // 먼저 String으로 받아서 로깅
            val responseString = restClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .header("x-goog-api-key", geminiApiKey)
                .body(request)
                .retrieve()
                .body(String::class.java)
                ?: throw IllegalStateException(ErrorMessage.GEMINI_API_REQUEST_FAILED.format("No response body"))

            logger.info("=== Raw Gemini API Response ===")
            logger.info(responseString)
            logger.info("================================")

            // 다시 파싱
            val objectMapper = com.fasterxml.jackson.module.kotlin.jacksonObjectMapper()
            val response = objectMapper.readValue(responseString, GeminiApiResponse::class.java)

            logger.info("Gemini API call successful")
            response
        } catch (e: Exception) {
            logger.error("Gemini API call failed", e)
            throw IllegalArgumentException(ErrorMessage.GEMINI_API_REQUEST_FAILED.format(e.message ?: "Unknown error"))
        }
    }

    private fun extractImageData(response: GeminiApiResponse): ByteArray {
        val candidate = response.candidates?.firstOrNull()
            ?: throw IllegalStateException(ErrorMessage.GEMINI_API_NO_CANDIDATES.message)

        val imagePart = candidate.content?.parts?.filterIsInstance<GeminiImagePart>()?.firstOrNull()
            ?: throw IllegalStateException(ErrorMessage.GEMINI_API_NO_IMAGE_DATA.message)

        logger.info("Extracting image data from response")
        return Base64.getDecoder().decode(imagePart.inlineData.data)
    }
}
