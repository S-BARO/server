package com.dh.baro.identity.domain

import com.dh.baro.core.AbstractTime
import com.dh.baro.core.IdGenerator
import com.dh.baro.core.AggregateRoot
import jakarta.persistence.*

@AggregateRoot
@Entity
@Table(name = "members")
class Member(
    @Id
    @Column(name = "id")
    val id: Long,

    @Column(name = "member_name", nullable = false)
    private var name: String,

    @Column(name = "email", nullable = false, unique = true)
    private var email: String,

    @Column(name = "phone_number", unique = true)
    private var phoneNumber: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "member_role", nullable = false, length = 20)
    val role: MemberRole = MemberRole.BUYER,

    @OneToMany(
        mappedBy = "member",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    private val socialAccounts: MutableList<SocialAccount> = mutableListOf(),
) : AbstractTime() {

    companion object {
        fun newMember(name: String, email: String): Member {
            return Member(
                id = IdGenerator.generate(),
                name = name,
                email = email,
            )
        }
    }
}
