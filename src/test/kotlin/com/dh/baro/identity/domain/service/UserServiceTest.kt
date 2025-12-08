package com.dh.baro.identity.domain.service

import com.dh.baro.identity.domain.User
import com.dh.baro.identity.domain.UserRole
import com.dh.baro.identity.domain.repository.SocialAccountRepository
import com.dh.baro.identity.domain.repository.UserRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.DisplayName
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource

@DataJpaTest
@ContextConfiguration(classes = [UserService::class])
@TestPropertySource("classpath:test.properties")
@EntityScan("com.dh.baro.identity.domain")
@EnableJpaRepositories("com.dh.baro.identity.domain.repository")
@DisplayName("UserService 클래스의")
internal class UserServiceTest(
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val socialAccountRepository: SocialAccountRepository,
) : DescribeSpec({

    lateinit var testUser: User

    beforeEach {
        testUser = userRepository.save(
            User(
                id = 1L,
                name = "홍길동",
                email = "hong@test.com",
                phoneNumber = "01012345678",
                address = "경기도 용인시",
                role = UserRole.BUYER,
            )
        )
    }

    afterEach {
        socialAccountRepository.deleteAll()
        userRepository.deleteAll()
    }

    describe("updateUserProfile 메서드는") {

        context("phoneNumber와 address를 모두 수정하는 경우") {
            it("두 필드가 모두 업데이트된다") {
                val newPhone = "01098765432"
                val newAddress = "서울시 강남구"

                val updatedUser = userService.updateUserProfile(testUser.id, newPhone, newAddress)

                updatedUser.getPhoneNumber() shouldBe newPhone
                updatedUser.getAddress() shouldBe newAddress
                updatedUser.getName() shouldBe "홍길동"
                updatedUser.getEmail() shouldBe "hong@test.com"
            }
        }

        context("phoneNumber만 수정하는 경우") {
            it("phoneNumber만 업데이트되고 address는 유지된다") {
                val newPhone = "01055556666"

                val updatedUser = userService.updateUserProfile(testUser.id, newPhone, testUser.getAddress())

                updatedUser.getPhoneNumber() shouldBe newPhone
                updatedUser.getAddress() shouldBe "경기도 용인시"
            }
        }

        context("address만 수정하는 경우") {
            it("address만 업데이트되고 phoneNumber는 유지된다") {
                val newAddress = "부산시 해운대구"

                val updatedUser = userService.updateUserProfile(testUser.id, testUser.getPhoneNumber(), newAddress)

                updatedUser.getPhoneNumber() shouldBe "01012345678"
                updatedUser.getAddress() shouldBe newAddress
            }
        }

        context("phoneNumber를 null로 변경하는 경우") {
            it("phoneNumber가 null로 업데이트된다") {
                val updatedUser = userService.updateUserProfile(testUser.id, null, testUser.getAddress())

                updatedUser.getPhoneNumber() shouldBe null
                updatedUser.getAddress() shouldBe "경기도 용인시"
            }
        }

        context("address를 null로 변경하는 경우") {
            it("address가 null로 업데이트된다") {
                val updatedUser = userService.updateUserProfile(testUser.id, testUser.getPhoneNumber(), null)

                updatedUser.getPhoneNumber() shouldBe "01012345678"
                updatedUser.getAddress() shouldBe null
            }
        }

        context("phoneNumber와 address를 모두 null로 변경하는 경우") {
            it("두 필드가 모두 null로 업데이트된다") {
                val updatedUser = userService.updateUserProfile(testUser.id, null, null)

                updatedUser.getPhoneNumber() shouldBe null
                updatedUser.getAddress() shouldBe null
            }
        }

        context("존재하지 않는 userId로 수정 시도하는 경우") {
            it("IllegalArgumentException이 발생한다") {
                val result = runCatching {
                    userService.updateUserProfile(999L, "01000000000", "주소")
                }

                result.isFailure shouldBe true
                result.exceptionOrNull() shouldNotBe null
                result.exceptionOrNull()!!::class shouldBe IllegalArgumentException::class
            }
        }

        context("트랜잭션 커밋 후") {
            it("데이터베이스에 변경사항이 반영된다") {
                val newPhone = "01077778888"
                val newAddress = "대전시 유성구"

                userService.updateUserProfile(testUser.id, newPhone, newAddress)

                val reloadedUser = userRepository.findById(testUser.id).get()
                reloadedUser.getPhoneNumber() shouldBe newPhone
                reloadedUser.getAddress() shouldBe newAddress
            }
        }
    }
})
