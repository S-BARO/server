package com.dh.baro.order.domain

import com.dh.baro.core.ErrorMessage
import com.dh.baro.order.application.OrderCreateCommand
import com.dh.baro.order.domain.service.OrderQueryService
import com.dh.baro.order.domain.service.OrderService
import com.dh.baro.order.productFixture
import com.dh.baro.product.domain.Product
import com.dh.baro.product.domain.repository.ProductRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import java.math.BigDecimal

@DataJpaTest
@ContextConfiguration(classes = [OrderService::class, OrderQueryService::class])
@TestPropertySource("classpath:test.properties")
@EntityScan(
    "com.dh.baro.order.domain",
    "com.dh.baro.product.domain",
)
@EnableJpaRepositories(
    "com.dh.baro.order.domain",
    "com.dh.baro.product.domain",
)
@DisplayName("OrderQueryService 클래스의")
internal class OrderQueryServiceTest(
    private val orderService: OrderService,
    private val orderQueryService: OrderQueryService,
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

    describe("getOrderDetailByUserId 메서드는") {

        context("정상 ID를 넘기면") {
            lateinit var savedOrder: Order
            beforeTest {
                val cmd = OrderCreateCommand(
                    userId = USER_ID,
                    shippingAddress = "Seoul",
                    orderItems = listOf(OrderCreateCommand.OrderItem(p1, 1))
                )
                savedOrder = orderService.createOrder(cmd)
            }

            it("주문 상세 + 항목을 fetch 한다") {
                val detail = shouldNotThrowAny { orderQueryService.getOrderDetailByUserId(savedOrder.id, USER_ID) }
                detail.items.first().productId shouldBe p1.id
            }
        }

        context("존재하지 않는 주문 ID를 넘기면") {
            val missingOrderId = 404L

            it("IllegalArgumentException를 던진다") {
                shouldThrow<IllegalArgumentException> {
                    orderQueryService.getOrderDetailByUserId(missingOrderId, USER_ID)
                }.message shouldBe ErrorMessage.ORDER_NOT_FOUND.format(missingOrderId)
            }
        }
    }

    describe("getOrdersByCursor 메서드는") {
        lateinit var o1: Order
        lateinit var o2: Order
        lateinit var o3: Order

        beforeEach {
            o1 = orderService.createOrder(
                OrderCreateCommand(
                    userId = USER_ID,
                    shippingAddress = "addr",
                    orderItems = listOf(OrderCreateCommand.OrderItem(p1, 1))
                )
            )
            o2 = orderService.createOrder(
                OrderCreateCommand(
                    userId = USER_ID,
                    shippingAddress = "addr",
                    orderItems = listOf(OrderCreateCommand.OrderItem(p2, 1))
                )
            )
            o3 = orderService.createOrder(
                OrderCreateCommand(
                    userId = USER_ID,
                    shippingAddress = "addr",
                    orderItems = listOf(OrderCreateCommand.OrderItem(p1, 1))
                )
            )
        }

        context("첫 페이지를 size=2 로 요청하면") {
            lateinit var slice: Slice<Order>

            beforeTest { slice = orderQueryService.getOrdersByCursor(USER_ID, null, 2) }

            it("요청 개수만큼 content 를 반환한다") {
                slice.content shouldHaveSize 2 // o3, o2
            }

            it("orderBy id desc 정렬") {
                slice.content.map { it.id } shouldContainExactly listOf(o3.id, o2.id) // o3, o2
            }

            it("hasNext 는 true 이다") {
                slice.hasNext() shouldBe true
            }
        }

        context("cursorId(o2) 이후 페이지를 요청하면") {
            lateinit var slice: Slice<Order>

            beforeTest { slice = orderQueryService.getOrdersByCursor(USER_ID, o2.id, 5) }

            it("cursor 아래의 주문(o1)만 반환하고 hasNext=false") {
                slice.content.map { it.id } shouldContainExactly listOf(o1.id)
                slice.hasNext() shouldBe false
            }
        }

        context("주문 내역이 하나도 없으면") {
            val otherUserId = 2L

            it("빈 slice 를 반환한다") {
                val slice = orderQueryService.getOrdersByCursor(otherUserId, null, 3)
                slice.content.shouldBeEmpty()
                slice.hasNext() shouldBe false
            }
        }
    }
}) {

    private companion object {
        private const val USER_ID = 1L
    }
}
