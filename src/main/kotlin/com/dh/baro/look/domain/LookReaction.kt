package com.dh.baro.look.domain

import com.dh.baro.core.BaseTimeEntity
import com.dh.baro.core.IdGenerator
import jakarta.persistence.*

@Entity
@Table(
    name = "look_reactions",
    uniqueConstraints = [
        UniqueConstraint(name = "UKmpvap1oy05x9bhc7u4hoe23ui", columnNames = ["user_id", "look_id"])
    ]
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