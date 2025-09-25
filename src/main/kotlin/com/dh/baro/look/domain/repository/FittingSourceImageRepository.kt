package com.dh.baro.look.domain.repository

import com.dh.baro.look.domain.FittingSourceImage
import com.dh.baro.look.domain.FittingSourceImageStatus
import org.springframework.data.jpa.repository.JpaRepository

interface FittingSourceImageRepository : JpaRepository<FittingSourceImage, Long> {
    fun findByUserIdAndUploadStatusOrderByIdDesc(userId: Long, uploadStatus: FittingSourceImageStatus): List<FittingSourceImage>
}
