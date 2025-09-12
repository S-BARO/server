package com.dh.baro.look.domain

import com.dh.baro.core.ErrorMessage
import com.dh.baro.look.application.LookCreateCommand
import com.dh.baro.look.domain.repository.LookImageRepository
import com.dh.baro.look.domain.repository.LookProductRepository
import com.dh.baro.look.domain.repository.LookRepository
import com.dh.baro.look.domain.service.LookService
import com.dh.baro.look.lookFixture
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource

@DataJpaTest
@ContextConfiguration(classes = [LookService::class])
@TestPropertySource("classpath:test.properties")
@EntityScan("com.dh.baro.look.domain")
@EnableJpaRepositories("com.dh.baro.look.domain")
@DisplayName("LookService 클래스의")
internal class LookServiceTest(
    private val lookService: LookService,
    private val lookRepository: LookRepository,
    private val lookImageRepository: LookImageRepository,
    private val lookProductRepository: LookProductRepository,
) : DescribeSpec({

    lateinit var look11: Look
    lateinit var look12: Look
    lateinit var look13: Look

    beforeEach {
        look11 = lookRepository.save(
            lookFixture(
                id = 11, title = "Look-11",
                imageUrls = listOf("u11-1", "u11-2"),
                productIds = listOf(101, 102),
            )
        )
        look12 = lookRepository.save(
            lookFixture(
                id = 12, title = "Look-12",
                imageUrls = listOf("u12-1"),
                productIds = listOf(201),
            )
        )
        look13 = lookRepository.save(
            lookFixture(
                id = 13, title = "Look-13",
                imageUrls = listOf("u13-1", "u13-2", "u13-3"),
                productIds = listOf(301, 302, 303),
            )
        )
    }

    afterEach {
        lookRepository.deleteAll()
    }

    describe("createLook 메서드는") {

        context("정상 입력이면") {
            lateinit var created: Look

            beforeTest {
                val cmd = LookCreateCommand(
                    creatorId = 1000L,
                    title = "새 룩",
                    description = "설명",
                    thumbnailUrl = "thumb://new",
                    imageUrls = listOf("a", "b", "c"),
                    productIds = listOf(1L, 2L, 3L),
                )
                created = shouldNotThrowAny { lookService.createLook(cmd) }
            }

            it("룩을 저장한다") {
                lookRepository.existsById(created.id) shouldBe true
            }

            it("이미지는 displayOrder=1..N 으로 저장된다") {
                val images = lookImageRepository.findByLookIdOrderByDisplayOrderAsc(created.id)
                images.map { it.displayOrder } shouldContainExactly listOf(1, 2, 3)
                images.map { it.imageUrl } shouldContainExactly listOf("a", "b", "c")
            }

            it("상품 연결은 중복 없이 순서대로 저장된다") {
                val lookProducts = lookProductRepository.findByLookIdOrderByDisplayOrderAsc(created.id)
                lookProducts.map { it.productId } shouldContainExactly listOf(1L, 2L, 3L)
                lookProducts.map { it.displayOrder } shouldContainExactly listOf(1, 2, 3)
            }
        }

        context("productIds 에 중복이 포함되면") {
            val cmd = LookCreateCommand(
                creatorId = 42L,
                title = "dup",
                description = null,
                thumbnailUrl = "t",
                imageUrls = listOf("x"),
                productIds = listOf(10L, 10L, 20L, 10L, 20L, 30L),
            )
            val created = lookService.createLook(cmd)

            it("중복은 제거되고 최초 등장 순서를 유지한다") {
                val lookProducts = lookProductRepository.findByLookIdOrderByDisplayOrderAsc(created.id)
                lookProducts.map { it.productId } shouldContainExactly listOf(10L, 20L, 30L)
                lookProducts.map { it.displayOrder } shouldContainExactly listOf(1, 2, 3)
            }
        }

        context("imageUrls 가 여러 개인 경우") {
            val cmd = LookCreateCommand(
                creatorId = 7L,
                title = "img-order",
                description = null,
                thumbnailUrl = "thumb",
                imageUrls = listOf("i1", "i2", "i3", "i4"),
                productIds = listOf(1L),
            )
            val created = lookService.createLook(cmd)

            it("입력 순서 그대로 displayOrder 가 지정된다") {
                val lookImages = lookImageRepository.findByLookIdOrderByDisplayOrderAsc(created.id)
                lookImages.map { it.imageUrl } shouldContainExactly listOf("i1", "i2", "i3", "i4")
                lookImages.map { it.displayOrder } shouldContainExactly listOf(1, 2, 3, 4)
            }
        }
    }

    describe("getLookDetail 메서드는") {

        context("존재하는 id 를 넘기면") {
            it("룩을 반환한다") {
                val found = shouldNotThrowAny { lookService.getLookDetail(look12.id) }
                found.getTitle() shouldBe "Look-12"
            }
        }

        context("존재하지 않는 id 를 넘기면") {
            val missingId = 404L

            it("IllegalArgumentException 를 던진다") {
                shouldThrow<IllegalArgumentException> {
                    lookService.getLookDetail(missingId)
                }.message shouldBe ErrorMessage.LOOK_NOT_FOUND.format(missingId)
            }
        }
    }

    describe("도메인 동작(추가 호출 가정)") {

        context("상품을 추가로 합치는 경우") {
            it("기존 displayOrder 다음 번호부터 부여된다") {
                // look11 은 productIds 101,102
                val look = lookRepository.findWithImagesAndProductsById(look11.id)
                    ?: error("look not found: ${look11.id}")

                look.addProducts(listOf(102, 103, 104)) // 102는 중복 → skip, 103/104 추가

                val savedLook = lookRepository.saveAndFlush(look)
                val lookProducts = lookProductRepository.findByLookIdOrderByDisplayOrderAsc(savedLook.id)

                lookProducts.map { it.productId } shouldContainExactly listOf(101L, 102L, 103L, 104L)
                lookProducts.map { it.displayOrder } shouldContainExactly listOf(1, 2, 3, 4)
            }
        }

        context("이미지를 추가로 붙이는 경우") {
            it("이미 존재 개수 다음 순서로 displayOrder 가 이어진다") {
                // look12 는 이미지 1개(u12-1 → order=1)
                val look = lookRepository.findWithImagesAndProductsById(look12.id)
                    ?: error("look not found: ${look12.id}")

                look.addImages(listOf("extra-1", "extra-2"))

                val savedLook = lookRepository.saveAndFlush(look)
                val lookImages = lookImageRepository.findByLookIdOrderByDisplayOrderAsc(savedLook.id)

                lookImages.map { it.imageUrl } shouldContainExactly listOf("u12-1", "extra-1", "extra-2")
                lookImages.map { it.displayOrder } shouldContainExactly listOf(1, 2, 3)
            }
        }
    }
})
