package com.dh.baro.look.domain

import com.dh.baro.core.BaseTimeEntity
import com.dh.baro.core.IdGenerator
import jakarta.persistence.*

@Entity
@Table(
    name = "look_reactions",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["user_id", "look_id"])
    ],
    indexes = [
        Index(
            name = "idx_look_reaction_user_type_id",
            columnList = "user_id, reaction_type, id DESC"
        ),
        Index(
            name = "idx_look_reaction_look_id",
            columnList = "look_id"
        )
    ],
)
class LookReaction(
    @Id
    @Column(name = "id")
    val id: Long,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "look_id", nullable = false)
    val lookId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false)
    val reactionType: ReactionType,
) : BaseTimeEntity() {

    override fun getId(): Long = id

    companion object {
        fun of(userId: Long, lookId: Long, reactionType: ReactionType): LookReaction {
            return LookReaction(
                id = IdGenerator.generate(),
                userId = userId,
                lookId = lookId,
                reactionType = reactionType,
            )
        }
    }
}
