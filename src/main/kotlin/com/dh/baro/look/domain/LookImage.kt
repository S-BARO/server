package com.dh.baro.look.domain

import com.dh.baro.core.AbstractTime
import jakarta.persistence.*

@Entity
@Table(name = "look_images")
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

    @Column(name = "is_thumbnail", nullable = false)
    val isThumbnail: Boolean = false
) : AbstractTime()
