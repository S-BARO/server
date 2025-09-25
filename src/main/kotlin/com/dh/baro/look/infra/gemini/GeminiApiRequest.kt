package com.dh.baro.look.infra.gemini

data class GeminiApiRequest(
    val contents: List<GeminiContent>,
)

data class GeminiContent(
    val parts: List<GeminiPart>,
)

sealed class GeminiPart

data class GeminiTextPart(
    val text: String,
) : GeminiPart()

data class GeminiImagePart(
    val inline_data: GeminiInlineData,
) : GeminiPart()

data class GeminiInlineData(
    val mime_type: String,
    val data: String,
)
