package com.dh.baro.look.domain.repository

import com.dh.baro.look.domain.Look
import org.springframework.data.domain.Slice

interface LookRepository {

    fun save(look: Look): Look

    fun existsById(id: Long): Boolean

    fun findWithImagesAndProductsById(id: Long): Look?

    fun findLooksForSwipe(
        userId: Long,
        cursorId: Long?,
        lookIds: List<Long>,
        size: Int,
    ): Slice<Look>

    fun incrementLike(lookId: Long): Int

    fun decrementLike(lookId: Long): Int

    fun deleteById(id: Long)
}
