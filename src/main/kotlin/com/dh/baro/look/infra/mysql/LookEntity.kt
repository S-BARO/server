package com.dh.baro.look.infra.mysql

import com.dh.baro.core.BaseTimeEntity
import com.dh.baro.core.annotation.AggregateRoot
import com.dh.baro.look.domain.Look
import jakarta.persistence.*

@AggregateRoot
@Entity
@Table(name = "looks")
class LookEntity(
    @Id
    @Column(name = "id")
    val id: Long,

    @Column(name = "creator_id", nullable = false)
    val creatorId: Long,

    @Column(name = "title", nullable = false)
    private var title: String,

    @Lob
    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    private var description: String? = null,

    @Column(name = "likes_count", nullable = false)
    private var likesCount: Int = 0,

    @Column(name = "thumbnail_url", nullable = false, length = 300)
    private var thumbnailUrl: String,

    @OneToMany(
        mappedBy = "look",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    private val images: MutableSet<LookImageEntity> = mutableSetOf(),

    @OneToMany(
        mappedBy = "look",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    private val lookProducts: MutableSet<LookProductEntity> = mutableSetOf(),
) : BaseTimeEntity() {

    override fun getId(): Long = id

    fun toDomain(): Look =
        Look(
            id = id,
            creatorId = creatorId,
            title = title,
            description = description,
            likesCount = likesCount,
            thumbnailUrl = thumbnailUrl,
            images = images.map { it.toDomain() },
            lookProducts = lookProducts.map { it.toDomain() },
            createdAt = createdAt,
            modifiedAt = modifiedAt,
        )

    companion object {
        fun fromDomain(look: Look): LookEntity =
            LookEntity(
                id = look.id,
                creatorId = look.creatorId,
                title = look.title,
                description = look.description,
                thumbnailUrl = look.thumbnailUrl,
                images = look.images.map {
                    LookImageEntity.fromDomain(look, it)
                }.toMutableSet(),
                lookProducts = look.lookProducts.map {
                    LookProductEntity.fromDomain(look, it)
                }.toMutableSet(),
            )
    }
}
