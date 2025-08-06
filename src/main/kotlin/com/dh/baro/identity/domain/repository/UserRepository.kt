package com.dh.baro.identity.domain.repository

import com.dh.baro.identity.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long>
