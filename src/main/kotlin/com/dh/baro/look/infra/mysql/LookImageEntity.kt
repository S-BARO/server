package com.dh.baro.look.infra.mysql

import com.dh.baro.core.BaseTimeEntity
import com.dh.baro.look.domain.Look
import com.dh.baro.look.domain.LookImage
import jakarta.persistence.*

@Entity
@Table(name = "look_images")
class LookImageEntity(
    @Id
    @Column(name = "id")
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "look_id", nullable = false)
    val look: LookEntity,

    @Column(name = "image_url", nullable = false, length = 300)
    val imageUrl: String,

    @Column(name = "display_order", nullable = false)
    val displayOrder: Int,
) : BaseTimeEntity() {

    override fun getId(): Long = id

    fun toDomain(): LookImage =
        LookImage(
            id = id,
            imageUrl = imageUrl,
            displayOrder = displayOrder,
        )

    companion object {
        fun fromDomain(look: Look, lookImage: LookImage): LookImageEntity =
            LookImageEntity(
                id = lookImage.id,
                look = LookEntity.fromDomain(look),
                imageUrl = lookImage.imageUrl,
                displayOrder = lookImage.displayOrder,
            )
    }
}
