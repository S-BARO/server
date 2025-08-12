package com.dh.baro.product.domain

import com.dh.baro.product.application.ProductCreateCommand
import com.dh.baro.product.domain.repository.CategoryRepository
import com.dh.baro.product.domain.repository.ProductRepository
import com.dh.baro.product.domain.service.ProductService
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import java.math.BigDecimal

@DataJpaTest
@ContextConfiguration(classes = [ProductService::class])
@TestPropertySource("classpath:test.properties")
@EntityScan("com.dh.baro.product.domain")
@EnableJpaRepositories("com.dh.baro.product.domain")
@DisplayName("ProductService 클래스의")
internal class ProductServiceTest(
    private val productService: ProductService,
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
) : DescribeSpec({

    lateinit var top: Category
    lateinit var shoe: Category

    beforeEach {
        top = categoryRepository.save(Category(1L, "TOP"))
        shoe = categoryRepository.save(Category(2L, "SHOE"))
    }

    afterEach {
        productRepository.deleteAll()
        categoryRepository.deleteAll()
    }

    describe("createProduct 메서드는") {
        val cmd = ProductCreateCommand(
            name = "Hoodie",
            price = BigDecimal("59000"),
            quantity = 10,
            description = "Nice Hoodie",
            likesCount = 0,
            thumbnailUrl = "https://example.com/hoodie-thumb.jpg",
            imageUrls = listOf(
                "https://example.com/hoodie-1.jpg",
                "https://example.com/hoodie-2.jpg"
            ),
            categoryIds = listOf(1L, 2L),
        )

        it("상품과 연결된 ProductCategory 를 모두 저장한다") {
            val saved = shouldNotThrowAny {
                productService.createProduct(cmd, listOf(top, shoe))
            }
            productRepository.existsById(saved.id) shouldBe true

            val categoryIds = saved.getProductCategories().map { it.category.id }
            categoryIds.shouldContainExactlyInAnyOrder(listOf(top.id, shoe.id))
        }
    }
})
