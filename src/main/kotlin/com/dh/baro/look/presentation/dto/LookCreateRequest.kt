package com.dh.baro.look.presentation.dto

import com.dh.baro.look.application.LookCreateCommand
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

    @field:Size(min = 1)
    val productIds: List<String>,
) {
    fun toCommand(creatorId: Long) = LookCreateCommand(
        creatorId = creatorId,
        title = title,
        description = description,
        thumbnailUrl = thumbnailUrl,
        imageUrls = imageUrls,
        productIds = productIds.map { it.toLong() },
    )
}
