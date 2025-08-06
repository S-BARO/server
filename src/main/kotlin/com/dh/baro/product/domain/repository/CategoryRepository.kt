package com.dh.baro.product.domain.repository

import com.dh.baro.product.domain.Category
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<Category, Long>
