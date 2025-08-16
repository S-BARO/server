package com.dh.baro.identity.domain.repository

import com.dh.baro.identity.domain.Store
import org.springframework.data.jpa.repository.JpaRepository

interface StoreRepository : JpaRepository<Store, Long>
