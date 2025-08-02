package com.dh.baro.product.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeSortedWith
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.DisplayName
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
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

        it("size 파라미터 만큼만 반환한다") {
            val list = productQueryService.getPopularProducts(null, null, size = 1)
            list.size shouldBe 1
            list.first() shouldBe p1
        }

        it("cursorId 이후 데이터만 반환한다") {
            val list = productQueryService.getPopularProducts(null, cursorId = p1.id, size = 10)
            list.shouldContainExactly(p2, p3)
        }

        it("카테고리 ID가 존재하지 않으면 빈 리스트") {
            val list = productQueryService.getPopularProducts(categoryId = 999L, cursorId = null, size = 10)
            list.shouldBeEmpty()
        }
    }

    describe("getNewestProducts 메서드는") {

        it("최근 30일 이내 상품만 반환하고 정렬은 id desc") {
            val list = productQueryService.getNewestProducts(null, null, 10)
            list.shouldContainExactly(p2, p1) // p2Id = 12, p1Id = 11
            list.shouldBeSortedWith(compareByDescending { it.id })
        }

        it("cursor 페이징 동작 확인") {
            val firstPage = productQueryService.getNewestProducts(null, null, 1) // p2
            val nextPage = productQueryService.getNewestProducts(null, cursorId = firstPage.last().id, size = 10)
            nextPage.shouldContainExactly(p1)
        }
    }

    describe("getProductDetail 메서드는") {

        it("존재하지 않는 id면 EntityNotFoundException") {
            shouldThrow<EntityNotFoundException> {
                productQueryService.getProductDetail(404L)
            }
        }

        it("상세 조회 시 이미지·카테고리 fetch 확인") {
            val detail = shouldNotThrowAny { productQueryService.getProductDetail(p1.id) }
            detail.images.isNotEmpty() shouldBe true
            detail.productCategories.first().category shouldBe top
        }
    }
})
