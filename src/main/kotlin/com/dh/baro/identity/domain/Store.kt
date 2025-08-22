package com.dh.baro.identity.domain

import com.dh.baro.core.BaseTimeEntity
import com.dh.baro.core.IdGenerator
import jakarta.persistence.*

@Entity
@Table(name = "stores")
class Store(
    @Id
    @Column(name = "id")
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    val owner: User,

    @Column(name = "store_name", nullable = false, length = 100)
    private var name: String,

    @Lob
    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    private var description: String? = null,

    @Column(name = "phone_number", length = 30)
    private var phoneNumber: String? = null,

    @Column(name = "address", length = 500)
    private var address: String? = null,

    @Column(name = "thumbnail_url", length = 300)
    private var thumbnailUrl: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "store_status", nullable = false, length = 20)
    private var status: StoreStatus = StoreStatus.DRAFT,
) : BaseTimeEntity() {

    override fun getId(): Long = id

    fun getName(): String = name

    fun getDescription(): String? = description

    fun getPhoneNumber(): String? = phoneNumber

    fun getAddress(): String? = address

    fun getThumbnailUrl(): String? = thumbnailUrl

    fun getStatus(): StoreStatus = status

    companion object {
        fun newStore(
            owner: User,
            name: String,
            description: String?,
            phoneNumber: String?,
            address: String?,
            thumbnailUrl: String?,
        ): Store {
            return Store(
                id = IdGenerator.generate(),
                owner = owner,
                name = name.trim(),
                description = description,
                phoneNumber = phoneNumber,
                address = address,
                thumbnailUrl = thumbnailUrl,
                status = StoreStatus.DRAFT,
            )
        }
    }
}
