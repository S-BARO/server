package com.dh.baro.look.application.dto

data class LookCreateCommand(
    val creatorId: Long,
    val title: String,
    val description: String?,
    val thumbnailUrl: String,
    val imageUrls: List<String>,
    val productIds: List<Long>,
)
