package com.dh.baro.look.infra.mongodb

import com.dh.baro.look.domain.Swipe
import com.dh.baro.look.domain.repository.SwipeRepository
import org.springframework.context.annotation.Primary
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Repository

@Primary
@Repository
class SwipeRepositoryImpl(
    private val swipeDocumentRepository: SwipeDocumentRepository,
) : SwipeRepository {

    override fun upsertSwipe(swipe: Swipe): Swipe {
        return try {
            val document = SwipeDocument.fromDomain(swipe)
            val saved = swipeDocumentRepository.save(document)
            saved.toDomain()
        } catch (ex: DataIntegrityViolationException) {
            val existing = findByUserIdAndLookId(swipe.userId, swipe.lookId)!!
            val updated = existing.copy(reactionType = swipe.reactionType)
            swipeDocumentRepository.save(SwipeDocument.fromDomain(updated)).toDomain()
        }
    }

    override fun findLookIdsByUserId(userId: Long): List<Long> {
        return swipeDocumentRepository.findLookIdsByUserId(userId)
            .map { it.getLookId() }
    }

    override fun findByUserIdAndLookId(userId: Long, lookId: Long): Swipe? {
        return swipeDocumentRepository.findByUserIdAndLookId(userId, lookId)
            ?.toDomain()
    }

    override fun deleteByUserIdAndLookId(userId: Long, lookId: Long): Long {
        return swipeDocumentRepository.deleteByUserIdAndLookId(userId, lookId)
    }

    override fun deleteLikeIfExists(userId: Long, lookId: Long): Int {
        return swipeDocumentRepository.deleteLikeByUserIdAndLookId(userId, lookId)
            .toInt()
    }
}
