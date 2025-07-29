package com.dh.baro.look.domain

import com.dh.baro.core.AbstractTime
import com.dh.baro.identity.domain.Member
import com.dh.baro.product.domain.Product
import jakarta.persistence.*

@Entity
@Table(name = "looks")
class Look(
    @Id
    @Column(name = "id")
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    val creator: Member? = null,

    @Column(name = "title", nullable = false)
    var title: String,

    @Lob @Column(name = "description")
    var description: String? = null,

    @Column(name = "likes_count", nullable = false)
    var likesCount: Int = 0,

    @OneToMany(mappedBy = "look", cascade = [CascadeType.ALL], orphanRemoval = true)
    val images: MutableList<LookImage> = mutableListOf(),

    @ManyToMany
    @JoinTable(
        name = "look_products",
        joinColumns = [JoinColumn(name = "look_id")],
        inverseJoinColumns = [JoinColumn(name = "product_id")]
    )
    val products: MutableSet<Product> = mutableSetOf()
) : AbstractTime()
