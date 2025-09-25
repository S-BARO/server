package com.dh.baro.look.infra.gemini

data class GeminiApiResponse(
    val candidates: List<GeminiCandidate>?,
    val usageMetadata: GeminiUsageMetadata?,
)

data class GeminiCandidate(
    val content: GeminiContent?,
    val finishReason: String?,
)

data class GeminiUsageMetadata(
    val promptTokenCount: Int?,
    val candidatesTokenCount: Int?,
    val totalTokenCount: Int?,
)
