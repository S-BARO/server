package com.dh.baro.order.domain

import com.dh.baro.order.application.OrderCreateCommand
import com.dh.baro.order.domain.service.OrderService
import com.dh.baro.order.productFixture
import com.dh.baro.product.domain.Product
import com.dh.baro.product.domain.repository.ProductRepository
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
                    shippingAddress = "Seoul",
                    orderItems = listOf(
                        OrderCreateCommand.OrderItem(p1, 2),
                        OrderCreateCommand.OrderItem(p2, 1),
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

            it("초기 상태는 ORDERED 이다") {
                order.status shouldBe OrderStatus.ORDERED
            }
        }
    }
}) {

    private companion object {
        private const val USER_ID = 1L
    }
}
