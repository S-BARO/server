package com.dh.baro.look.infra.mongodb

import com.dh.baro.look.domain.Swipe
import com.dh.baro.look.domain.repository.SwipeRepository
import org.springframework.stereotype.Repository

@Repository
class SwipeMongoRepository(
    private val swipeDocumentRepository: SwipeDocumentRepository,
) : SwipeRepository {

    override fun save(swipe: Swipe): Swipe {
        val document = SwipeDocument.fromDomain(swipe)
        val saved = swipeDocumentRepository.save(document)
        return saved.toDomain()
    }

    override fun existsByUserIdAndLookId(userId: Long, lookId: Long): Boolean {
        return swipeDocumentRepository.existsByUserIdAndLookId(userId, lookId)
    }

    override fun findByUserIdAndLookId(userId: Long, lookId: Long): Swipe? {
        return swipeDocumentRepository.findByUserIdAndLookId(userId, lookId)
            ?.toDomain()
    }

    override fun findUserSwipeHistory(userId: Long): List<Swipe> {
        return swipeDocumentRepository.findByUserIdOrderByIdDesc(userId)
            .map { it.toDomain() }
    }

    override fun deleteByUserIdAndLookId(userId: Long, lookId: Long): Long {
        return swipeDocumentRepository.deleteByUserIdAndLookId(userId, lookId)
    }

    override fun deleteLikeIfExists(userId: Long, lookId: Long): Int {
        return swipeDocumentRepository.deleteLikeByUserIdAndLookId(userId, lookId)
            .toInt()
    }
}
