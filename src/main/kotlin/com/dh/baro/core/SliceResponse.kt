package com.dh.baro.core

import org.springframework.data.domain.Slice

data class SliceResponse<T>(
    val content: List<T>,
    val hasNext: Boolean,
    val nextCursor: Any?,
) {

    companion object {
        fun <T, R> from(
            slice: Slice<T>,
            mapper: (T) -> R,
            cursorExtractor: (T) -> Any,
        ): SliceResponse<R> =
            SliceResponse(
                content = slice.content.map(mapper),
                hasNext = slice.hasNext(),
                nextCursor = slice.content.lastOrNull()?.let(cursorExtractor),
            )
    }
}
