package com.dh.baro.identity.domain

import com.dh.baro.core.AbstractTime
import jakarta.persistence.*

@Entity
@Table(
    name = "social_accounts",
    uniqueConstraints = [UniqueConstraint(columnNames = ["provider", "provider_id"])]
)
class SocialAccount(
    @Id
    @Column(name = "id")
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 30)
    val provider: AuthProvider,

    @Column(name = "provider_id", nullable = false, length = 200)
    val providerId: String
) : AbstractTime()
