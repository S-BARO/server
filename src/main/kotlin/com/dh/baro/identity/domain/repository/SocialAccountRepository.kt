package com.dh.baro.identity.domain.repository

import com.dh.baro.identity.domain.AuthProvider
import com.dh.baro.identity.domain.SocialAccount
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface SocialAccountRepository : JpaRepository<SocialAccount, Long> {
    @EntityGraph(attributePaths = ["user"])
    fun findByProviderAndProviderId(provider: AuthProvider, providerId: String): SocialAccount?
}
