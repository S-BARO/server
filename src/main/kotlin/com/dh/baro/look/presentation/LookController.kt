package com.dh.baro.look.presentation

import com.dh.baro.core.Cursor
import com.dh.baro.core.SliceResponse
import com.dh.baro.core.auth.CurrentUser
import com.dh.baro.look.application.LookFacade
import com.dh.baro.look.application.LookReactionFacade
import com.dh.baro.look.presentation.dto.*
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/looks")
class LookController(
    private val lookFacade: LookFacade,
    private val lookReactionFacade: LookReactionFacade,
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createLook(
        @CurrentUser userId: Long,
        @Valid @RequestBody request: LookCreateRequest,
    ): LookCreateResponse =
        LookCreateResponse.from(lookFacade.createLook(request.toCommand(userId)))

    @PutMapping("/{lookId}/reaction")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun recordReaction(
        @CurrentUser userId: Long,
        @PathVariable lookId: Long,
        @Valid @RequestBody request: ReactionRequest,
    ) = lookReactionFacade.recordReaction(
        userId = userId,
        lookId = lookId,
        reactionType = request.reactionType,
    )

    @DeleteMapping("/{lookId}/reaction")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun cancelReaction(
        @CurrentUser userId: Long,
        @PathVariable lookId: Long,
    ) = lookReactionFacade.cancelReaction(userId, lookId)

    @GetMapping("/swipe")
    @ResponseStatus(HttpStatus.OK)
    fun getSwipeLooks(
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
    fun detail(@PathVariable lookId: Long): LookDetailResponse =
        lookFacade.getLookDetail(lookId)
}
