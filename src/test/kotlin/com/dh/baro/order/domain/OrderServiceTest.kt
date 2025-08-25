package com.dh.baro.order.domain

import com.dh.baro.core.ErrorMessage
import com.dh.baro.core.exception.ConflictException
import com.dh.baro.order.application.OrderCreateCommand
import com.dh.baro.order.productFixture
import com.dh.baro.product.domain.Product
import com.dh.baro.product.domain.repository.ProductRepository
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
) : DescribeSpec({

    lateinit var p1: Product
    lateinit var p2: Product
    lateinit var p3: Product

    beforeEach {
        p1 = productFixture(
            id = 11,
            name = "A",
            price = BigDecimal("1000"),
            quantity = 5
        )
        p2 = productFixture(
            id = 12,
            name = "B",
            price = BigDecimal("2000"),
            quantity = 3
        )
        p3 = productFixture(
            id = 13,
            name = "C",
            price = BigDecimal("1500"),
            quantity = 0
        )
        
        productRepository.save(p1)
        productRepository.save(p2)
        productRepository.save(p3)
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
                    productList = listOf(p1, p2),
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
                order.items.first { it.productId == p1.id }.quantity shouldBe 2
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
                    productList = listOf(p1),
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

        context("productList에 없는 상품 ID가 items에 포함되면") {
            val missingId = 999L
            val cmd = OrderCreateCommand(
                userId = USER_ID,
                productList = listOf(p1), // p1만 있음
                shippingAddress = "Seoul",
                items = listOf(OrderCreateCommand.Item(missingId, 1)) // 999는 productList에 없음
            )

            it("IllegalArgumentException을 던진다") {
                shouldThrow<IllegalArgumentException> {
                    orderService.createOrder(cmd)
                }.message shouldBe ErrorMessage.PRODUCT_NOT_FOUND.format(missingId)
            }
        }

        context("동일 상품이 두 번 전달되면") {
            val cmd = OrderCreateCommand(
                userId = USER_ID,
                productList = listOf(p1),
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
                userId = USER_ID,
                productList = listOf(p3),
                shippingAddress = "addr",
                items = listOf(OrderCreateCommand.Item(p3.id, 1))
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
    }
}
