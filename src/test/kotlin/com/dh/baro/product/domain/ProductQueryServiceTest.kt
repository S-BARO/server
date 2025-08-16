package com.dh.baro.product.domain

import com.dh.baro.core.ErrorMessage
import com.dh.baro.core.SliceResponse
import com.dh.baro.product.domain.repository.CategoryRepository
import com.dh.baro.product.domain.repository.ProductRepository
import com.dh.baro.product.domain.service.ProductQueryService
import com.dh.baro.product.presentation.dto.ProductListItem
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeSortedWith
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource

@DataJpaTest
@ContextConfiguration(classes = [ProductQueryService::class])
@TestPropertySource("classpath:test.properties")
@EntityScan("com.dh.baro.product.domain")
@EnableJpaRepositories("com.dh.baro.product.domain")
@DisplayName("ProductQueryService 클래스의")
internal class ProductQueryServiceTest(
    private val productQueryService: ProductQueryService,
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) : DescribeSpec({

    lateinit var top: Category
    lateinit var shoe: Category
    lateinit var p1: Product;
    lateinit var p2: Product;
    lateinit var p3: Product

    beforeEach {
        top = categoryRepository.save(categoryFixture(1, "TOP"))
        shoe = categoryRepository.save(categoryFixture(2, "SHOE"))

        p1 = productRepository.save(productFixture(11, "T-Shirt", top, likes = 300))
        p2 = productRepository.save(productFixture(12, "Hoodie", top, likes = 200))
        p3 = productRepository.save(productFixture(21, "Sneakers", shoe, likes = 500, createdAtAgoDays = 31))
    }

    afterEach { productRepository.deleteAll() }

    describe("getPopularProducts 메서드는") {
        context("size 만큼만 요청하면") {
            val requestedSize = 1

            it("가장 인기 상품부터 size 개수만 반환한다") {
                val expectedId = p3.id
                val result = productQueryService.getPopularProducts(
                    categoryId = null,
                    cursorLikes = null,
                    cursorId = null,
                    size = requestedSize,
                )
                result.size shouldBe requestedSize
                result.first().id shouldBe expectedId
            }
        }

        context("cursor(likes,id)를 넘기면") {
            val cursorLikes = p1.getLikesCount()
            val cursorId = p1.id
            val expectedIds = listOf(p2.id)

            it("cursor 이후의 데이터만 반환한다") {
                val result = productQueryService.getPopularProducts(
                    categoryId = null,
                    cursorLikes = cursorLikes,
                    cursorId = cursorId,
                    size = 10,
                )
                result.map { it.id } shouldContainExactly expectedIds
            }
        }

        context("존재하지 않는 카테고리 ID를 넘기면") {
            val missingCategoryId = 999L

            it("빈 리스트를 반환한다") {
                val result = productQueryService.getPopularProducts(
                    categoryId = missingCategoryId,
                    cursorLikes = null,
                    cursorId = null,
                    size = 10,
                )
                result.shouldBeEmpty()
            }
        }
    }

    describe("getNewestProducts 메서드는") {
        context("cursor 없이 요청하면") {
            val expectedIds = listOf(p2.id, p1.id)

            it("최근 30일 이내 상품을 id DESC 로 반환한다") {
                val result = productQueryService.getNewestProducts(
                    categoryId = null,
                    cursorId = null,
                    size = 10,
                )
                result.map { it.id } shouldContainExactly expectedIds
                result.shouldBeSortedWith(compareByDescending { it.id })
            }
        }

        context("cursorId 를 넘기면") {
            val cursorId = p2.id
            val expectedId = listOf(p1.id)

            it("cursor 이후 데이터만 반환한다") {
                val result = productQueryService.getNewestProducts(
                    categoryId = null,
                    cursorId = cursorId,
                    size = 10,
                )

                result.map { it.id } shouldContainExactly expectedId
            }
        }
    }

    describe("getProductDetail 메서드는") {
        context("존재하지 않는 id면") {
            val nonExistentProductId = 404L

            it("IllegalArgumentException를 반환한다") {
                shouldThrow<IllegalArgumentException> {
                    productQueryService.getProductDetail(nonExistentProductId)
                }.message shouldBe ErrorMessage.PRODUCT_NOT_FOUND.format(nonExistentProductId)
            }
        }

        context("상품 상세 조회 시") {
            it("연관 엔티티(Images·Categories)를 fetch join 으로 즉시 로딩한다") {
                val detail = shouldNotThrowAny { productQueryService.getProductDetail(p1.id) }
                detail.getImages().isNotEmpty() shouldBe true
                detail.getProductCategories().first().category.id shouldBe top.id
            }
        }
    }

    describe("Slice 기능") {
        context("인기 상품 slice 조회 (hasNext = true)") {
            val requestSize = 1
            lateinit var slice: Slice<Product>

            beforeTest {
                slice = productQueryService.getPopularProducts(
                    categoryId = null,
                    cursorLikes = null,
                    cursorId = null,
                    size = requestSize,
                )
            }

            it("content 크기는 요청 size 와 동일하다") {
                slice.content.size shouldBe requestSize
            }

            it("hasNext 는 true 이다") {
                slice.hasNext() shouldBe true
            }
        }

        context("인기 상품 slice 조회 (hasNext = false)") {
            val firstPageSize = 2
            lateinit var nextSlice: Slice<Product>

            beforeTest {
                val firstSlice = productQueryService.getPopularProducts(null, null, null, firstPageSize)
                val lastItem = firstSlice.content.last()
                nextSlice = productQueryService.getPopularProducts(
                    categoryId = null,
                    cursorLikes = lastItem.getLikesCount(),
                    cursorId = lastItem.id,
                    size = firstPageSize,
                )
            }

            it("다음 페이지에 남은 데이터(p2)만 반환하고 hasNext 는 false") {
                val expectedId = p2.id
                nextSlice.content.map { it.id } shouldContainExactly listOf(expectedId)
                nextSlice.hasNext() shouldBe false
            }
        }
    }
})
