package com.dh.baro.identity.domain.service

import com.dh.baro.identity.domain.*
import com.dh.baro.identity.domain.dto.SocialUserInfo
import com.dh.baro.identity.domain.repository.MemberRepository
import com.dh.baro.identity.domain.repository.SocialAccountRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val memberRepo: MemberRepository,
    private val socialRepo: SocialAccountRepository,
) {

    @Transactional
    fun findOrRegister(
        provider: AuthProvider,
        info: SocialUserInfo
    ): Pair<Member, Boolean> {
        val existing = socialRepo.findByProviderAndProviderId(provider, info.providerId)
        if (existing.isPresent) {
            return existing.get().member to false
        }

        val member = memberRepo.findByEmail(info.email).orElseGet {
            memberRepo.save(
                Member(
                    id = 0,
                    name = info.nickname ?: "사용자",
                    email = info.email
                )
            )
        }

        val isBrandNew = existing.isEmpty

        socialRepo.save(
            SocialAccount(
                id = 0,
                member = member,
                provider = provider,
                providerId = info.providerId
            )
        )

        return member to isBrandNew
    }
}
