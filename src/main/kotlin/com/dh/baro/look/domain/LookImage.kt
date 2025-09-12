package com.dh.baro.look.domain

import com.dh.baro.core.BaseTimeEntity
import com.dh.baro.core.IdGenerator
import jakarta.persistence.*

@Entity
@Table(
    name = "look_images",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_look_image_look_display_order", columnNames = ["look_id", "display_order"])
    ]
)
class LookImage(
    @Id
    @Column(name = "id")
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "look_id", nullable = false)
    val look: Look,

    @Column(name = "image_url", nullable = false, length = 300)
    val imageUrl: String,

    @Column(name = "display_order", nullable = false)
    val displayOrder: Int,
) : BaseTimeEntity() {

    override fun getId(): Long = id

    companion object {
        fun of(
            look: Look,
            imageUrl: String,
            displayOrder: Int,
        ): LookImage =
            LookImage(
                id = IdGenerator.generate(),
                look = look,
                imageUrl = imageUrl,
                displayOrder = displayOrder,
            )
    }
}
