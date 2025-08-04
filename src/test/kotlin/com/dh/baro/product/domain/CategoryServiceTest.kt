package com.dh.baro.product.domain

import com.dh.baro.core.ErrorMessage
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource

@DataJpaTest
@ContextConfiguration(classes = [CategoryService::class])
@TestPropertySource("classpath:test.properties")
@EntityScan("com.dh.baro.product.domain")
@EnableJpaRepositories("com.dh.baro.product.domain")
@DisplayName("CategoryService 클래스의")
internal class CategoryServiceTest(
    private val categoryService: CategoryService,
    private val categoryRepository: CategoryRepository,
) : DescribeSpec({

    afterEach { categoryRepository.deleteAll() }

    describe("createCategory 메서드는") {

        it("새 ID이면 저장 후 반환한다") {
            val saved = shouldNotThrowAny { categoryService.createCategory(10L, "TOP") }
            saved.id shouldBe 10L
            categoryRepository.existsById(10L) shouldBe true
        }

        context("이미 존재하는 ID이면") {
            val existingCategoryId = 1L
            it("IllegalArgumentException를 발생시킨다") {
                categoryRepository.save(Category(existingCategoryId, "TOP"))
                shouldThrow<IllegalArgumentException> {
                    categoryService.createCategory(1L, "NEW_NAME")
                }.message shouldBe ErrorMessage.CATEGORY_ALREADY_EXISTS.format(existingCategoryId)
            }
        }
    }

    describe("getCategoriesByIds 메서드는") {

        lateinit var ids: List<Long>

        beforeEach {
            categoryRepository.saveAll(
                listOf(Category(1L, "A"), Category(2L, "B"), Category(3L, "C"))
            )
            ids = listOf(1L, 2L, 3L)
        }

        it("모든 ID가 존재하면 Category 리스트를 반환한다") {
            val result = shouldNotThrowAny { categoryService.getCategoriesByIds(ids) }
            result.map { it.id }.shouldContainExactly(ids)
        }

        it("일부 ID가 없으면 IllegalArgumentException를 발생시킨다") {
            shouldThrow<IllegalArgumentException> {
                categoryService.getCategoriesByIds(listOf(1L, 99L))
            }.message shouldBe ErrorMessage.CATEGORY_NOT_FOUND.format(listOf(1L, 99L))
        }

        it("빈 리스트를 입력하면 빈 리스트를 반환한다") {
            val result = categoryService.getCategoriesByIds(emptyList())
            result.shouldBeEmpty()
        }
    }
})
