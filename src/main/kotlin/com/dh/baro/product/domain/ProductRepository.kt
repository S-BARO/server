package com.dh.baro.product.domain

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant

interface ProductRepository : JpaRepository<Product, Long> {

    @Query("""
        select p from Product p
        where (:categoryId is null or exists (
                 select 1 from ProductCategory pc
                 where pc.product = p and pc.category.id = :categoryId ))
          and (:cursorLikes is null
             or p.likesCount < :cursorLikes
             or (p.likesCount = :cursorLikes and p.id < :cursorId))
        order by p.likesCount desc, p.id desc
    """)
    fun findPopularProductsByCursor(
        @Param("categoryId") categoryId: Long?,
        @Param("cursorLikes") cursorLikes: Int?,
        @Param("cursorId") cursorId: Long?,
        pageable: Pageable,
    ): List<Product>

    @Query("""
        select p from Product p
        where p.createdAt >= :cutoff
          and (:categoryId is null or exists (
                 select 1 from ProductCategory pc
                 where pc.product = p and pc.category.id = :categoryId ))
          and (:cursorId is null or p.id < :cursorId)
        order by p.id desc
    """)
    fun findNewestProductsByCursor(
        @Param("cutoff") cutoff: Instant,
        @Param("categoryId") categoryId: Long?,
        @Param("cursorId") cursorId: Long?,
        @Param("size") size: Int,
    ): List<Product>

    @Query("""
        select p from Product p
        left join fetch p.images
        left join fetch p.productCategories pc
        left join fetch pc.category
        where p.id = :id
    """)
    fun findDetailProductById(@Param("id") id: Long): Product?
}
