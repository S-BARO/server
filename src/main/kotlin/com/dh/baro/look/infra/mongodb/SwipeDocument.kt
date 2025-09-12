package com.dh.baro.look.infra.mongodb

import com.dh.baro.look.domain.Swipe
import com.dh.baro.look.domain.ReactionType
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.Instant

@Document(collection = "swipes")
@CompoundIndexes(
    CompoundIndex(
        name = "idx_user_id_desc",
        def = "{'user_id': 1, '_id': -1}"
    ), CompoundIndex(
        name = "idx_user_look_unique",
        def = "{'user_id': 1, 'look_id': 1}",
        unique = true
    ), CompoundIndex(
        name = "idx_look_reaction",
        def = "{'look_id': 1, 'reaction_type': 1}"
    )
)
data class SwipeDocument(
    @Id
    val id: Long,

    @Field("user_id")
    val userId: Long,

    @Field("look_id")
    val lookId: Long,

    @Field("reaction_type")
    val reactionType: ReactionType,

    @CreatedDate
    @Field("created_at")
    val createdAt: Instant? = null,
) {

    fun toDomain(): Swipe =
        Swipe(
            id = id,
            userId = userId,
            lookId = lookId,
            reactionType = reactionType,
            createdAt = createdAt,
        )

    companion object {
        fun fromDomain(swipe: Swipe): SwipeDocument =
            SwipeDocument(
                id = swipe.id,
                userId = swipe.userId,
                lookId = swipe.lookId,
                reactionType = swipe.reactionType,
            )
    }
}
