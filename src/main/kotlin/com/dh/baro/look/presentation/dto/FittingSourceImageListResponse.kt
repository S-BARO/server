package com.dh.baro.look.presentation.dto

import com.dh.baro.look.domain.FittingSourceImage

data class FittingSourceImageListResponse(
    val images: List<FittingSourceImageDto>,
) {
    companion object {
        fun from(images: List<FittingSourceImage>): FittingSourceImageListResponse =
            FittingSourceImageListResponse(
                images = images.map(FittingSourceImageDto::from)
            )
    }
}
