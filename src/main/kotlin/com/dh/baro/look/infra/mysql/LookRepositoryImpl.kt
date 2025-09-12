package com.dh.baro.look.infra.mysql

import com.dh.baro.look.domain.Look
import com.dh.baro.look.domain.repository.LookRepository
import org.springframework.context.annotation.Primary
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Primary
@Repository
class LookRepositoryImpl(
    private val lookJpaRepository: LookJpaRepository,
) : LookRepository {

    override fun save(look: Look): Look =
        lookJpaRepository.save(LookEntity.fromDomain(look)).toDomain()

    override fun existsById(id: Long): Boolean {
        return lookJpaRepository.existsById(id)
    }

    override fun findWithImagesAndProductsById(id: Long): Look? {
        return lookJpaRepository.findWithImagesAndProductsById(id)?.toDomain()
    }

    override fun findLooksForSwipe(
        userId: Long,
        cursorId: Long?,
        lookIds: List<Long>,
        size: Int,
    ): Slice<Look> {
        return lookJpaRepository.findLooksForSwipe(userId, cursorId, lookIds, PageRequest.of(0, size))
            .map { lookEntity -> lookEntity.toDomain() }
    }

    override fun incrementLike(lookId: Long): Int {
        return lookJpaRepository.incrementLikesCount(lookId)
    }

    override fun decrementLike(lookId: Long): Int {
        return lookJpaRepository.decrementLikesCount(lookId)
    }

    override fun deleteById(id: Long) {
        lookJpaRepository.deleteById(id)
    }
}
