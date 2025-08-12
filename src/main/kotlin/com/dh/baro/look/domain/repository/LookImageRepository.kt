package com.dh.baro.look.domain.repository

import com.dh.baro.look.domain.LookImage
import org.springframework.data.jpa.repository.JpaRepository

interface LookImageRepository : JpaRepository<LookImage, Long> {
    fun findByLookIdOrderByDisplayOrderAsc(lookId: Long): List<LookImage>
}
