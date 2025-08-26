package com.dh.baro.look.domain

import com.dh.baro.look.domain.repository.LookReactionRepository
import com.dh.baro.look.domain.repository.LookRepository
import com.dh.baro.look.domain.service.LookReactionService
import com.dh.baro.look.lookFixture
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource

@DataJpaTest
@ContextConfiguration(classes = [LookReactionService::class])
@TestPropertySource("classpath:test.properties")
@EntityScan("com.dh.baro.look.domain")
@EnableJpaRepositories("com.dh.baro.look.domain")
@DisplayName("LookReactionService 클래스의")
internal class LookReactionServiceTest(
    private val lookReactionService: LookReactionService,
    private val lookRepository: LookRepository,
    private val lookReactionRepository: LookReactionRepository,
) : DescribeSpec({

    val userA = 101L
    val userB = 202L

    lateinit var look1: Look
    lateinit var look2: Look

    beforeEach {
        look1 = lookRepository.save(
            lookFixture(
                id = 11, title = "L-11",
                imageUrls = listOf("i1-1", "i1-2"), productIds = listOf(1001, 1002)
            )
        )
        look2 = lookRepository.save(
            lookFixture(
                id = 12, title = "L-12",
                imageUrls = listOf("i2-1"), productIds = listOf(2001)
            )
        )
    }

    afterEach {
        lookReactionRepository.deleteAll()
        lookRepository.deleteAll()
    }

    fun likes(lookId: Long) = lookRepository.findById(lookId).get().getLikesCount()

    describe("createReactionIfAbsent 메서드는") {

        context("처음 LIKE를 생성하면") {
            beforeTest {
                shouldNotThrowAny {
                    lookReactionService.createReactionIfAbsent(userA, look1.id, ReactionType.LIKE)
                }
            }

            it("반응 레코드가 생성되고, 룩의 likesCount가 1 증가한다") {
                likes(look1.id) shouldBe 1
                lookReactionRepository.existsByUserIdAndLookId(userA, look1.id) shouldBe true
            }
        }

        context("이미 LIKE가 있는 상태에서 다시 호출하면") {
            beforeTest {
                lookReactionService.createReactionIfAbsent(userA, look1.id, ReactionType.LIKE)
                likes(look1.id) shouldBe 1

                // 재호출
                lookReactionService.createReactionIfAbsent(userA, look1.id, ReactionType.LIKE)
            }

            it("아무 변화가 없다(멱등). likesCount는 변하지 않는다") {
                likes(look1.id) shouldBe 1
            }
        }

        context("처음 DISLIKE를 생성하면") {
            beforeTest {
                lookReactionService.createReactionIfAbsent(userA, look1.id, ReactionType.DISLIKE)
            }

            it("반응 레코드만 생성되고, likesCount는 증가하지 않는다") {
                likes(look1.id) shouldBe 0
                lookReactionRepository.existsByUserIdAndLookId(userA, look1.id) shouldBe true
            }
        }

        context("이미 DISLIKE가 있는 상태에서 같은 요청을 재호출하면") {
            beforeTest {
                lookReactionService.createReactionIfAbsent(userA, look1.id, ReactionType.DISLIKE)
                likes(look1.id) shouldBe 0

                // 재호출
                lookReactionService.createReactionIfAbsent(userA, look1.id, ReactionType.DISLIKE)
            }

            it("아무 변화가 없다(멱등). likesCount는 그대로") {
                likes(look1.id) shouldBe 0
            }
        }

        context("이미 반응이 있는데 다른 타입으로 생성하려 하면") {
            beforeTest {
                lookReactionService.createReactionIfAbsent(userA, look1.id, ReactionType.DISLIKE)
                likes(look1.id) shouldBe 0

                // LIKE로 다시 호출
                lookReactionService.createReactionIfAbsent(userA, look1.id, ReactionType.LIKE)
            }

            it("서비스는 '없으면 생성'만 수행하므로 아무 변화가 없다") {
                likes(look1.id) shouldBe 0
            }
        }
    }

    describe("deleteReaction 메서드는") {

        context("LIKE 반응을 삭제하면") {
            beforeTest {
                lookReactionService.createReactionIfAbsent(userA, look1.id, ReactionType.LIKE)
                likes(look1.id) shouldBe 1

                shouldNotThrowAny {
                    lookReactionService.deleteReaction(userA, look1.id)
                }
            }

            it("반응 레코드가 삭제되고, likesCount가 1 감소한다(바닥은 0)") {
                likes(look1.id) shouldBe 0
                lookReactionRepository.existsByUserIdAndLookId(userA, look1.id) shouldBe false
            }
        }

        context("LIKE 삭제 후, 다시 삭제를 반복 호출해도") {
            beforeTest {
                lookReactionService.createReactionIfAbsent(userA, look1.id, ReactionType.LIKE)
                likes(look1.id) shouldBe 1

                // 첫 삭제
                lookReactionService.deleteReaction(userA, look1.id)
                likes(look1.id) shouldBe 0

                // 두 번째 삭제(없음)
                shouldNotThrowAny {
                    lookReactionService.deleteReaction(userA, look1.id)
                }
            }

            it("멱등하게 동작한다(추가 감소 없음, 예외 없음)") {
                // 0 유지
                likes(look1.id) shouldBeGreaterThanOrEqual 0
            }
        }

        context("DISLIKE 반응을 삭제하면") {
            beforeTest {
                lookReactionService.createReactionIfAbsent(userA, look1.id, ReactionType.DISLIKE)
                likes(look1.id) shouldBe 0

                lookReactionService.deleteReaction(userA, look1.id)
            }

            it("반응 레코드만 사라지고, likesCount는 변하지 않는다") {
                likes(look1.id) shouldBe 0
                lookReactionRepository.existsByUserIdAndLookId(userA, look1.id) shouldBe false
            }
        }

        context("반응이 존재하지 않을 때 삭제하면") {
            it("아무 일도 일어나지 않는다(멱등)") {
                likes(look1.id) shouldBe 0
                shouldNotThrowAny {
                    lookReactionService.deleteReaction(userA, look1.id)
                }
                likes(look1.id) shouldBe 0
            }
        }

        context("여러 유저가 같은 룩에 LIKE 한 상태에서 한 명이 삭제하면") {
            it("likesCount는 1만 감소한다") {
                lookReactionService.createReactionIfAbsent(userA, look1.id, ReactionType.LIKE)
                lookReactionService.createReactionIfAbsent(userB, look1.id, ReactionType.LIKE)
                likes(look1.id) shouldBe 2

                lookReactionService.deleteReaction(userA, look1.id)
                likes(look1.id) shouldBe 1

                lookReactionRepository.existsByUserIdAndLookId(userB, look1.id) shouldBe true
            }
        }

        context("서로 다른 룩에 대한 삭제가 상호 간섭하지 않는다") {
            it("look1 삭제가 look2 카운트에 영향을 주지 않는다") {
                lookReactionService.createReactionIfAbsent(userA, look1.id, ReactionType.LIKE)
                lookReactionService.createReactionIfAbsent(userA, look2.id, ReactionType.LIKE)
                likes(look1.id) shouldBe 1
                likes(look2.id) shouldBe 1

                lookReactionService.deleteReaction(userA, look1.id)

                likes(look1.id) shouldBe 0
                likes(look2.id) shouldBe 1
            }
        }
    }
})
