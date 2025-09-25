package com.dh.baro.look.presentation

import com.dh.baro.core.annotation.CurrentUser
import com.dh.baro.look.application.FitFacade
import com.dh.baro.look.presentation.dto.*
import com.dh.baro.look.presentation.swagger.FitSwagger
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/fit")
class FitController(
    private val fitFacade: FitFacade,
) : FitSwagger {

    @PostMapping("/source-images/upload-url")
    @ResponseStatus(HttpStatus.OK)
    override fun createUploadUrl(
        @CurrentUser userId: Long,
    ): FittingSourceImageUploadUrlResponse =
        FittingSourceImageUploadUrlResponse.from(
            fitFacade.generateUploadUrl(userId)
        )

    @PutMapping("/source-images/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun completeImageUpload(
        @CurrentUser userId: Long,
        @PathVariable imageId: Long,
    ) = fitFacade.completeImageUpload(imageId, userId)

    @GetMapping("/source-images")
    @ResponseStatus(HttpStatus.OK)
    override fun getUserFittingSourceImages(
        @CurrentUser userId: Long,
    ): FittingSourceImageListResponse =
        FittingSourceImageListResponse.from(
            fitFacade.getUserFittingSourceImages(userId)
        )

    @PostMapping("/ai-fitting")
    fun generateAiFitting(
        @CurrentUser userId: Long,
        @Valid @RequestBody request: AiFittingRequest,
    ): ResponseEntity<ByteArray> {
        val fittingResult = fitFacade.generateAiFitting(
            request.sourceImageUrl,
            request.clothingImageUrl
        )

        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_PNG)
            .body(fittingResult.generatedImageData)
    }
}
