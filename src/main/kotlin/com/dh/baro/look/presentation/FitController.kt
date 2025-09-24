package com.dh.baro.look.presentation

import com.dh.baro.core.annotation.CurrentUser
import com.dh.baro.look.application.FittingSourceImageFacade
import com.dh.baro.look.presentation.dto.*
import com.dh.baro.look.presentation.swagger.FitSwagger
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/fit")
class FitController(
    private val fittingSourceImageFacade: FittingSourceImageFacade,
) : FitSwagger {

    @PostMapping("/source-images/upload-url")
    @ResponseStatus(HttpStatus.OK)
    override fun createUploadUrl(
        @CurrentUser userId: Long,
    ): FittingSourceImageUploadUrlResponse =
        FittingSourceImageUploadUrlResponse.from(
            fittingSourceImageFacade.generateUploadUrl(userId)
        )

    @PutMapping("/source-images/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun completeImageUpload(
        @CurrentUser userId: Long,
        @PathVariable imageId: Long,
    ) = fittingSourceImageFacade.completeImageUpload(imageId, userId)

    @GetMapping("/source-images")
    @ResponseStatus(HttpStatus.OK)
    override fun getUserFittingSourceImages(
        @CurrentUser userId: Long,
    ): FittingSourceImageListResponse =
        FittingSourceImageListResponse.from(
            fittingSourceImageFacade.getUserFittingSourceImages(userId)
        )
}
