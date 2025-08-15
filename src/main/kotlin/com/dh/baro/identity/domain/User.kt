package com.dh.baro.identity.domain

import com.dh.baro.core.AbstractTime
import com.dh.baro.core.IdGenerator
import com.dh.baro.core.AggregateRoot
import jakarta.persistence.*

@AggregateRoot
@Entity
@Table(name = "users")
class User(
    @Id
    @Column(name = "id")
    val id: Long,

    @Column(name = "user_name", nullable = false)
    private var name: String,

    @Column(name = "email", nullable = false, unique = true)
    private var email: String,

    @Column(name = "phone_number", unique = true)
    private var phoneNumber: String? = null,

    @Column(name = "address", length = 500)
    private var address: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false, length = 20)
    val role: UserRole = UserRole.BUYER,

    @OneToMany(
        mappedBy = "user",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    private val socialAccounts: MutableList<SocialAccount> = mutableListOf(),
) : AbstractTime() {

    fun getName() = name

    fun getEmail() = email

    fun getPhoneNumber() = phoneNumber

    fun getAddress() = address

    companion object {
        fun newUser(name: String, email: String): User {
            return User(
                id = IdGenerator.generate(),
                name = name,
                email = email,
            )
        }
    }
}
