package com.dh.baro.look.infra.mongodb

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface SwipeDocumentRepository : MongoRepository<SwipeDocument, String> {

    fun existsByUserIdAndLookId(userId: Long, lookId: Long): Boolean

    fun findByUserIdOrderByIdDesc(userId: Long): List<SwipeDocument>

    fun findByUserIdAndLookId(userId: Long, lookId: Long): SwipeDocument?

    fun deleteByUserIdAndLookId(userId: Long, lookId: Long): Long

    @Query(value = "{'user_id': ?0, 'look_id': ?1, 'reaction_type': 'LIKE'}", delete = true)
    fun deleteLikeByUserIdAndLookId(userId: Long, lookId: Long): Long
}
