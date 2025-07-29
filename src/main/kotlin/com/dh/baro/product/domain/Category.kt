package com.dh.baro.product.domain

import jakarta.persistence.*

@Entity
@Table(name = "categories")
class Category(
    @Id
    @Column(name = "category_id")
    val id: Long,

    @Column(name = "category_name", unique = true, nullable = false, length = 50)
    val name: String
)
