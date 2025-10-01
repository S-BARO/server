package com.dh.baro.look.infra.gemini

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeminiApiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GeminiGenerationConfig? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeminiGenerationConfig(
    val responseModalities: List<String>? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeminiContent(
    val parts: List<GeminiPart>,
)

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonIgnoreProperties(ignoreUnknown = true)
sealed class GeminiPart

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeminiTextPart(
    val text: String,
) : GeminiPart()

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeminiImagePart(
    @JsonProperty("inline_data")
    val inlineData: GeminiInlineData,
) : GeminiPart()

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeminiInlineData(
    @JsonProperty("mime_type")
    val mimeType: String,
    val data: String,
)
