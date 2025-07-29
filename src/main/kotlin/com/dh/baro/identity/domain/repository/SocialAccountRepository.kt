package com.dh.baro.identity.domain.repository

import com.dh.baro.identity.domain.AuthProvider
import com.dh.baro.identity.domain.SocialAccount
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface SocialAccountRepository : JpaRepository<SocialAccount, Long> {
    fun findByProviderAndProviderId(
        provider: AuthProvider,
        providerId: String,
    ): Optional<SocialAccount>
}
