package com.dh.baro.look.domain.repository

import com.dh.baro.look.domain.LookProduct
import org.springframework.data.jpa.repository.JpaRepository

interface LookProductRepository : JpaRepository<LookProduct, Int> {
    fun findByLookIdOrderByDisplayOrderAsc(lookId: Long): List<LookProduct>
}
