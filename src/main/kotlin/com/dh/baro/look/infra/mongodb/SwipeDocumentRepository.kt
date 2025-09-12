package com.dh.baro.look.infra.mongodb

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface SwipeDocumentRepository : MongoRepository<SwipeDocument, String> {

    @Query("{ 'user_id': ?0 }")
    fun findLookIdsByUserId(userId: Long): List<LookIdProjection>

    fun findByUserIdAndLookId(userId: Long, lookId: Long): SwipeDocument?

    fun deleteByUserIdAndLookId(userId: Long, lookId: Long): Long

    @Query(value = "{'user_id': ?0, 'look_id': ?1, 'reaction_type': 'LIKE'}", delete = true)
    fun deleteLikeByUserIdAndLookId(userId: Long, lookId: Long): Long
}

interface LookIdProjection {
    fun getLookId(): Long
}
