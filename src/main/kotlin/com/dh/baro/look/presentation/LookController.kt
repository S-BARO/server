package com.dh.baro.look.presentation

import com.dh.baro.core.Cursor
import com.dh.baro.core.SliceResponse
import com.dh.baro.core.annotation.CurrentUser
import com.dh.baro.look.application.LookFacade
import com.dh.baro.look.application.LookReactionFacade
import com.dh.baro.look.application.SwipeFacade
import com.dh.baro.look.presentation.dto.*
import com.dh.baro.look.presentation.dto.LookReactionRequest
import com.dh.baro.look.presentation.swagger.LookSwagger
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/looks")
class LookController(
    private val lookFacade: LookFacade,
    private val swipeFacade: SwipeFacade,
    private val lookReactionFacade: LookReactionFacade,
) : LookSwagger {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    override fun createLook(
        @CurrentUser userId: Long,
        @Valid @RequestBody request: LookCreateRequest,
    ): LookCreateResponse =
        LookCreateResponse.from(lookFacade.createLook(request.toCommand(userId)))

    @PutMapping("/{lookId}/swipe")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun recordSwipe(
        @CurrentUser userId: Long,
        @PathVariable lookId: Long,
        @Valid @RequestBody request: SwipeRequest,
    ) = swipeFacade.recordSwipe(
        userId = userId,
        lookId = lookId,
        reactionType = request.reactionType,
    )

    @PutMapping("/{lookId}/swipe2")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun recordSwipe2(
        @RequestParam userId: Long,
        @PathVariable lookId: Long,
        @Valid @RequestBody request: SwipeRequest,
    ) = swipeFacade.recordSwipe(
        userId = userId,
        lookId = lookId,
        reactionType = request.reactionType,
    )

    @PutMapping("/{lookId}/reaction")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun recordLookReaction(
        @RequestParam userId: Long,
        @PathVariable lookId: Long,
        @Valid @RequestBody request: LookReactionRequest,
    ) = lookReactionFacade.recordLookReaction(
        userId = userId,
        lookId = lookId,
        reactionType = request.reactionType,
    )

    @DeleteMapping("/{lookId}/swipe")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun cancelSwipe(
        @CurrentUser userId: Long,
        @PathVariable lookId: Long,
    ) = swipeFacade.cancelSwipe(userId, lookId)

    @GetMapping("/swipe")
    @ResponseStatus(HttpStatus.OK)
    override fun getSwipeLooks(
        @CurrentUser userId: Long,
        @RequestParam(required = false) cursorId: Long?,
        @RequestParam(defaultValue = "10") size: Int,
    ): SliceResponse<LookDto> {
        val slice = lookFacade.getSwipeLooks(userId, cursorId, size)
        return SliceResponse.from(
            slice,
            mapper = LookDto::from,
            cursorExtractor = { Cursor(it.id) }
        )
    }

    @GetMapping("/{lookId}")
    @ResponseStatus(HttpStatus.OK)
    override fun getLookDetail(@PathVariable lookId: Long): LookDetailResponse {
        return lookFacade.getLookDetail(lookId)
    }
}
