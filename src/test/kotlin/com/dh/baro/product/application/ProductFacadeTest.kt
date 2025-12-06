package com.dh.baro.product.application

import com.dh.baro.identity.domain.Store
import com.dh.baro.identity.domain.User
import com.dh.baro.identity.domain.repository.StoreRepository
import com.dh.baro.identity.domain.repository.UserRepository
import com.dh.baro.identity.domain.service.StoreService
import com.dh.baro.product.categoryFixture
import com.dh.baro.product.domain.ProductLike
import com.dh.baro.product.domain.repository.CategoryRepository
import com.dh.baro.product.domain.repository.ProductLikeRepository
import com.dh.baro.product.domain.repository.ProductRepository
import com.dh.baro.product.domain.service.CategoryService
import com.dh.baro.product.domain.service.ProductLikeService
import com.dh.baro.product.domain.service.ProductQueryService
import com.dh.baro.product.domain.service.ProductService
import com.dh.baro.product.presentation.dto.ProductListItem
import com.dh.baro.product.productFixture
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource

@DataJpaTest
@ContextConfiguration(classes = [
    ProductFacade::class,
    ProductService::class,
    ProductQueryService::class,
    ProductLikeService::class,
    CategoryService::class,
    StoreService::class,
])
@TestPropertySource("classpath:test.properties")
@EntityScan("com.dh.baro.product.domain", "com.dh.baro.identity.domain")
@EnableJpaRepositories("com.dh.baro.product.domain.repository", "com.dh.baro.identity.domain.repository")
@DisplayName("ProductFacade 클래스의")
internal class ProductFacadeTest(
    private val productFacade: ProductFacade,
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val storeRepository: StoreRepository,
    private val userRepository: UserRepository,
    private val productLikeRepository: ProductLikeRepository,
) : DescribeSpec({

    lateinit var owner: User
    lateinit var store: Store

    beforeEach {
        owner = userRepository.save(
            User(
                id = 1L,
                name = "Store Owner",
                email = "owner@test.com"
            )
        )
        store = storeRepository.save(
            Store(
                id = 123L,
                owner = owner,
                name = "Test Store",
            )
        )
    }

    afterEach {
        productLikeRepository.deleteAll()
        productRepository.deleteAll()
        storeRepository.deleteAll()
        userRepository.deleteAll()
        categoryRepository.deleteAll()
    }

    describe("getPopularProducts 메서드는") {

        context("userId가 null인 경우") {
            beforeTest {
                val category = categoryRepository.save(categoryFixture(1L, "TOP"))
                productRepository.save(productFixture(11L, "Product-1", category, likes = 100))
                productRepository.save(productFixture(12L, "Product-2", category, likes = 200))
            }

            it("isLiked가 null이다") {
                val bundle = productFacade.getPopularProducts(
                    categoryId = null,
                    cursorLikes = null,
                    cursorId = null,
                    size = 10,
                    userId = null
                )

                bundle.likedProductIds shouldBe null
                bundle.productSlice.content shouldHaveSize 2

                val storeMap = bundle.storeList.associateBy { it.id }
                val items = bundle.productSlice.content.map { p ->
                    ProductListItem.ofOrNull(p, storeMap, bundle.likedProductIds)
                }

                items.forEach { item ->
                    item?.isLiked shouldBe null
                }
            }
        }

        context("userId가 있고 좋아요를 하지 않은 경우") {
            beforeTest {
                val category = categoryRepository.save(categoryFixture(1L, "TOP"))
                productRepository.save(productFixture(11L, "Product-1", category, likes = 100))
                productRepository.save(productFixture(12L, "Product-2", category, likes = 200))
            }

            it("isLiked가 false이다") {
                val userId = 999L
                val bundle = productFacade.getPopularProducts(
                    categoryId = null,
                    cursorLikes = null,
                    cursorId = null,
                    size = 10,
                    userId = userId
                )

                bundle.likedProductIds shouldBe emptySet()
                bundle.productSlice.content shouldHaveSize 2

                val storeMap = bundle.storeList.associateBy { it.id }
                val items = bundle.productSlice.content.map { p ->
                    ProductListItem.ofOrNull(p, storeMap, bundle.likedProductIds)
                }

                items.forEach { item ->
                    item?.isLiked shouldBe false
                }
            }
        }

        context("userId가 있고 일부 상품만 좋아요한 경우") {
            val userId = 777L
            val likedProductId = 11L
            val notLikedProductId = 12L

            beforeTest {
                val category = categoryRepository.save(categoryFixture(1L, "TOP"))
                productRepository.save(productFixture(likedProductId, "Product-1", category, likes = 100))
                productRepository.save(productFixture(notLikedProductId, "Product-2", category, likes = 200))

                productLikeRepository.save(ProductLike.create(userId, likedProductId))
            }

            it("좋아요한 상품은 isLiked=true, 안한 상품은 isLiked=false") {
                val bundle = productFacade.getPopularProducts(
                    categoryId = null,
                    cursorLikes = null,
                    cursorId = null,
                    size = 10,
                    userId = userId
                )

                bundle.likedProductIds shouldBe setOf(likedProductId)
                bundle.productSlice.content shouldHaveSize 2

                val storeMap = bundle.storeList.associateBy { it.id }
                val items = bundle.productSlice.content.associateBy { it.id }.mapValues { (_, p) ->
                    ProductListItem.ofOrNull(p, storeMap, bundle.likedProductIds)
                }

                items[likedProductId]?.isLiked shouldBe true
                items[notLikedProductId]?.isLiked shouldBe false
            }
        }

        context("userId가 있고 모든 상품을 좋아요한 경우") {
            val userId = 888L

            beforeTest {
                val category = categoryRepository.save(categoryFixture(1L, "TOP"))
                val p1 = productRepository.save(productFixture(11L, "Product-1", category, likes = 100))
                val p2 = productRepository.save(productFixture(12L, "Product-2", category, likes = 200))
                val p3 = productRepository.save(productFixture(13L, "Product-3", category, likes = 300))

                productLikeRepository.save(ProductLike.create(userId, p1.id))
                productLikeRepository.save(ProductLike.create(userId, p2.id))
                productLikeRepository.save(ProductLike.create(userId, p3.id))
            }

            it("모든 상품의 isLiked가 true이다") {
                val bundle = productFacade.getPopularProducts(
                    categoryId = null,
                    cursorLikes = null,
                    cursorId = null,
                    size = 10,
                    userId = userId
                )

                bundle.likedProductIds shouldBe setOf(11L, 12L, 13L)
                bundle.productSlice.content shouldHaveSize 3

                val storeMap = bundle.storeList.associateBy { it.id }
                val items = bundle.productSlice.content.map { p ->
                    ProductListItem.ofOrNull(p, storeMap, bundle.likedProductIds)
                }

                items.forEach { item ->
                    item?.isLiked shouldBe true
                }
            }
        }

        context("다른 userId의 좋아요는 영향을 주지 않는 경우") {
            val userA = 100L
            val userB = 200L
            val productId = 11L

            beforeTest {
                val category = categoryRepository.save(categoryFixture(1L, "TOP"))
                productRepository.save(productFixture(productId, "Product-1", category, likes = 100))
                productRepository.save(productFixture(12L, "Product-2", category, likes = 200))

                productLikeRepository.save(ProductLike.create(userA, productId))
            }

            it("userB는 좋아요하지 않았으므로 isLiked=false") {
                val bundle = productFacade.getPopularProducts(
                    categoryId = null,
                    cursorLikes = null,
                    cursorId = null,
                    size = 10,
                    userId = userB
                )

                bundle.likedProductIds shouldBe emptySet()

                val storeMap = bundle.storeList.associateBy { it.id }
                val items = bundle.productSlice.content.map { p ->
                    ProductListItem.ofOrNull(p, storeMap, bundle.likedProductIds)
                }

                items.forEach { item ->
                    item?.isLiked shouldBe false
                }
            }
        }

        context("인기순 정렬 및 페이지네이션") {
            val userId = 555L

            beforeTest {
                val category = categoryRepository.save(categoryFixture(1L, "TOP"))
                productRepository.save(productFixture(11L, "Low", category, likes = 100))
                productRepository.save(productFixture(12L, "Medium", category, likes = 200))
                productRepository.save(productFixture(13L, "High", category, likes = 300))

                productLikeRepository.save(ProductLike.create(userId, 13L))
            }

            it("좋아요 수 많은 순으로 정렬되고 isLiked가 올바르게 설정된다") {
                val bundle = productFacade.getPopularProducts(
                    categoryId = null,
                    cursorLikes = null,
                    cursorId = null,
                    size = 10,
                    userId = userId
                )

                bundle.productSlice.content.map { it.id } shouldContainExactly listOf(13L, 12L, 11L)
                bundle.likedProductIds shouldBe setOf(13L)

                val storeMap = bundle.storeList.associateBy { it.id }
                val items = bundle.productSlice.content.associateBy { it.id }.mapValues { (_, p) ->
                    ProductListItem.ofOrNull(p, storeMap, bundle.likedProductIds)
                }

                items[13L]?.isLiked shouldBe true
                items[12L]?.isLiked shouldBe false
                items[11L]?.isLiked shouldBe false
            }
        }
    }
})
