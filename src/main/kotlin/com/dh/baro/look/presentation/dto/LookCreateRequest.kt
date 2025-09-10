package com.dh.baro.look.presentation.dto

import com.dh.baro.core.StringListToLongListDeserializer
import com.dh.baro.look.application.LookCreateCommand
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LookCreateRequest(
    @field:NotBlank
    @field:Size(max = 100)
    val title: String,

    val description: String?,

    @field:NotBlank
    val thumbnailUrl: String,

    @field:Size(min = 1)
    val imageUrls: List<@NotBlank String>,

    @JsonDeserialize(using = StringListToLongListDeserializer::class)
    @field:Size(min = 1)
    val productIds: List<Long>,
) {
    fun toCommand(creatorId: Long) = LookCreateCommand(
        creatorId = creatorId,
        title = title,
        description = description,
        thumbnailUrl = thumbnailUrl,
        imageUrls = imageUrls,
        productIds = productIds,
    )
}
