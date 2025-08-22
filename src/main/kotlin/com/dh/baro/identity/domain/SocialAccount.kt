package com.dh.baro.identity.domain

import com.dh.baro.core.BaseTimeEntity
import com.dh.baro.core.IdGenerator
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
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 30)
    val provider: AuthProvider,

    @Column(name = "provider_id", nullable = false, length = 200)
    val providerId: String
) : BaseTimeEntity() {

    override fun getId(): Long = id

    companion object {
        fun of(user: User, provider: AuthProvider, providerId: String): SocialAccount {
            return SocialAccount(
                id = IdGenerator.generate(),
                user = user,
                provider = provider,
                providerId = providerId,
            )
        }
    }
}
