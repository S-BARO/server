package com.dh.baro.look.domain.repository

import com.dh.baro.look.domain.FittingSourceImage
import org.springframework.data.jpa.repository.JpaRepository

interface FittingSourceImageRepository : JpaRepository<FittingSourceImage, Long> {
    fun findByUserIdOrderByIdDesc(userId: Long): List<FittingSourceImage>
}
