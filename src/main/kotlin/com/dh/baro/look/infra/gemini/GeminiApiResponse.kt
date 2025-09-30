package com.dh.baro.look.infra.gemini

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeminiApiResponse(
    val candidates: List<GeminiCandidate>?,
    val usageMetadata: GeminiUsageMetadata?,
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
    @JsonProperty("inline_data")
    val inlineData: GeminiResponseInlineData,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeminiResponseInlineData(
    val data: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeminiUsageMetadata(
    val promptTokenCount: Int?,
    val candidatesTokenCount: Int?,
    val totalTokenCount: Int?,
)
