package com.dh.baro.identity.domain

import com.dh.baro.core.AbstractTime
import com.dh.baro.core.converter.MemberRoleConverter
import jakarta.persistence.*

@Entity
@Table(name = "members")
class Member(
    @Id
    @Column(name = "id")
    val id: Long,

    @Column(name = "member_name", nullable = false)
    var name: String,

    @Column(name = "email", unique = true)
    var email: String? = null,

    @Column(name = "phone_number", unique = true)
    var phoneNumber: String? = null,

    @Convert(converter = MemberRoleConverter::class)
    @Column(name = "member_role", nullable = false, length = 20)
    var role: MemberRole = MemberRole.BUYER,

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], orphanRemoval = true)
    val socialAccounts: MutableList<SocialAccount> = mutableListOf(),

    @Version
    @Column(name = "row_version")
    val version: Long? = null
) : AbstractTime()
