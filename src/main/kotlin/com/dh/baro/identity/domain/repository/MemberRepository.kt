package com.dh.baro.identity.domain.repository

import com.dh.baro.identity.domain.Member
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface MemberRepository : JpaRepository<Member, Long> {
    fun findByEmail(email: String?): Optional<Member>
}
