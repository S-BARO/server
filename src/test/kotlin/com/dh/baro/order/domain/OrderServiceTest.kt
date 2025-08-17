package com.dh.baro.order.domain

import com.dh.baro.core.ErrorMessage
import com.dh.baro.core.exception.ConflictException
import com.dh.baro.order.application.OrderCreateCommand
import com.dh.baro.order.categoryFixture
import com.dh.baro.order.productFixture
import com.dh.baro.product.domain.Product
import com.dh.baro.product.domain.repository.ProductRepository
import com.dh.baro.product.domain.Category
import com.dh.baro.product.domain.repository.CategoryRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import java.math.BigDecimal

@DataJpaTest
@ContextConfiguration(classes = [OrderService::class])
@TestPropertySource("classpath:test.properties")
@EntityScan(
    "com.dh.baro.order.domain",
    "com.dh.baro.product.domain",
)
@EnableJpaRepositories(
    "com.dh.baro.order.domain",
    "com.dh.baro.product.domain",
)
@DisplayName("OrderService 클래스의")
internal class OrderServiceTest(
    private val orderService: OrderService,
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
) : DescribeSpec({

    lateinit var top: Category
    lateinit var p1: Product
    lateinit var p2: Product
    lateinit var p3: Product

    beforeEach {
        top = categoryRepository.save(categoryFixture(1, "TOP"))
        p1 = productRepository.save(productFixture1)
        p2 = productRepository.save(productFixture2)
        p3 = productRepository.save(productFixture3)
    }

    afterEach {
        orderRepository.deleteAll()
        productRepository.deleteAll()
    }

    describe("createOrder 메서드는") {
        context("정상 입력이면") {
            lateinit var order: Order

            beforeTest {
                val cmd = OrderCreateCommand(
                    userId = USER_ID,
                    shippingAddress = "Seoul",
                    items = listOf(
                        OrderCreateCommand.Item(p1.id, 2),
                        OrderCreateCommand.Item(p2.id, 1),
                    )
                )
                order = shouldNotThrowAny { orderService.createOrder(cmd) }
            }

            it("주문을 저장한다") {
                orderRepository.existsById(order.id) shouldBe true
            }

            it("총금액은 각 항목 가격 합계이다") {
                order.totalPrice shouldBe BigDecimal("4000")
            }

            it("항목 개수와 수량이 정확하다") {
                order.items.shouldHaveSize(2)
                order.items.first { it.product.id == p1.id }.quantity shouldBe 2
            }

            it("상품 재고를 차감한다") {
                productRepository.findById(p1.id).get().getQuantity() shouldBe 3
            }

            it("초기 상태는 ORDERED 이다") {
                order.status shouldBe OrderStatus.ORDERED
            }
        }

        context("재고가 부족하면") {

            it("ConflictException를 던지고 롤백한다") {
                val cmd = OrderCreateCommand(
                    userId = USER_ID,
                    shippingAddress = "Seoul",
                    items = listOf(OrderCreateCommand.Item(p1.id, 99))
                )

                shouldThrow<ConflictException> {
                    orderService.createOrder(cmd)
                }.message shouldBe ErrorMessage.OUT_OF_STOCK.format(p1.id)

                // 롤백 확인
                orderRepository.count() shouldBe 0
                productRepository.findById(p1.id).get().getQuantity() shouldBe 5
            }
        }

        context("존재하지 않는 상품 ID가 포함되면") {
            val missingId = 999L
            val cmd = OrderCreateCommand(
                userId = USER_ID,
                shippingAddress = "Seoul",
                items = listOf(OrderCreateCommand.Item(missingId, 1))
            )

            it("IllegalArgumentException를 던진다") {
                shouldThrow<IllegalArgumentException> {
                    orderService.createOrder(cmd)
                }.message shouldBe ErrorMessage.PRODUCT_NOT_FOUND.format(missingId)
            }
        }

        context("동일 상품이 두 번 전달되면") {
            val cmd = OrderCreateCommand(
                userId = USER_ID,
                shippingAddress = "Seoul",
                items = listOf(
                    OrderCreateCommand.Item(p1.id, 1),
                    OrderCreateCommand.Item(p1.id, 2),
                )
            )

            it("각 항목을 그대로 저장하고 합산하여 재고를 차감한다") {
                val order = orderService.createOrder(cmd)

                order.items.shouldHaveSize(1) // p1 하나만
                productRepository.findById(p1.id).get().getQuantity() shouldBe 2 // 5-(1+2)
            }
        }

        context("재고가 0인 상품이 포함되면") {
            val cmd = OrderCreateCommand(
                USER_ID, "addr",
                listOf(OrderCreateCommand.Item(p3.id, 1))
            )

            it("OUT_OF_STOCK 예외가 발생한다") {
                shouldThrow<ConflictException> {
                    orderService.createOrder(cmd)
                }.message shouldBe ErrorMessage.OUT_OF_STOCK.format(p3.id)
            }
        }
    }
}) {

    private companion object {
        private const val USER_ID = 1L
        private val categoryFixture = categoryFixture(1, "TOP")
        private val productFixture1 = productFixture(11, "A", categoryFixture, price = BigDecimal("1000"), quantity = 5)
        private val productFixture2 = productFixture(12, "B", categoryFixture, price = BigDecimal("2000"), quantity = 3)
        private val productFixture3 = productFixture(13, "C", categoryFixture, price = BigDecimal("1500"), quantity = 0)
    }
}
