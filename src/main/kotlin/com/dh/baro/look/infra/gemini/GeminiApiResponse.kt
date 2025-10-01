package com.dh.baro.look.infra.gemini

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeminiApiResponse(
    val candidates: List<GeminiCandidate>?,
    val promptFeedback: GeminiPromptFeedback?,
    val usageMetadata: GeminiUsageMetadata?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeminiPromptFeedback(
    val blockReason: String?,
    val blockReasonMessage: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeminiCandidate(
    val content: GeminiResponseContent?,
    val finishReason: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeminiResponseContent(
    val parts: List<GeminiResponsePart>?,
    val role: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeminiResponsePart(
    val inlineData: GeminiResponseInlineData?,
    val text: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeminiResponseInlineData(
    val mimeType: String?,
    val data: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeminiUsageMetadata(
    val promptTokenCount: Int?,
    val candidatesTokenCount: Int?,
    val totalTokenCount: Int?,
)
