package com.dh.baro.order.domain

import com.dh.baro.core.ErrorMessage
import com.dh.baro.order.application.OrderCreateCommand
import com.dh.baro.product.domain.Product
import com.dh.baro.product.domain.ProductRepository
import com.dh.baro.product.domain.Category
import com.dh.baro.product.domain.CategoryRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
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
                    userId = defaultUserId,
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
                productRepository.findById(p1.id).get().quantity shouldBe 3
            }

            it("초기 상태는 ORDERED 이다") {
                order.status shouldBe OrderStatus.ORDERED
            }
        }

        context("재고가 부족하면") {

            it("IllegalArgumentException를 던지고 롤백한다") {
                val cmd = OrderCreateCommand(
                    userId = defaultUserId,
                    shippingAddress = "Seoul",
                    items = listOf(OrderCreateCommand.Item(p1.id, 99))
                )

                shouldThrow<IllegalArgumentException> {
                    orderService.createOrder(cmd)
                }.message shouldBe ErrorMessage.OUT_OF_STOCK.format(p1.id)

                // 롤백 확인
                orderRepository.count() shouldBe 0
                productRepository.findById(p1.id).get().quantity shouldBe 5
            }
        }

        context("존재하지 않는 상품 ID가 포함되면") {
            val missingId = 999L
            val cmd = OrderCreateCommand(
                userId = defaultUserId,
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
                userId = defaultUserId,
                shippingAddress = "Seoul",
                items = listOf(
                    OrderCreateCommand.Item(p1.id, 1),
                    OrderCreateCommand.Item(p1.id, 2),
                )
            )

            it("각 항목을 그대로 저장하고 합산하여 재고를 차감한다") {
                val order = orderService.createOrder(cmd)

                order.items.shouldHaveSize(1) // p1 하나만
                productRepository.findById(p1.id).get().quantity shouldBe 2 // 5-(1+2)
            }
        }

        context("재고가 0인 상품이 포함되면") {
            val cmd = OrderCreateCommand(
                defaultUserId, "addr",
                listOf(OrderCreateCommand.Item(p3.id, 1))
            )

            it("OUT_OF_STOCK 예외가 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    orderService.createOrder(cmd)
                }.message shouldBe ErrorMessage.OUT_OF_STOCK.format(p3.id)
            }
        }
    }

    describe("getOrderDetail 메서드는") {

        context("정상 ID를 넘기면") {
            lateinit var savedOrder: Order
            beforeTest {
                val cmd = OrderCreateCommand(
                    defaultUserId, "Seoul", listOf(OrderCreateCommand.Item(productFixture1.id, 1))
                )
                savedOrder = orderService.createOrder(cmd)
            }

            it("주문 상세 + 항목 + 상품을 fetch 한다") {
                val detail = shouldNotThrowAny { orderService.getOrderDetail(defaultUserId, savedOrder.id) }
                detail.items.first().product.shouldNotBeNull()
            }
        }

        context("존재하지 않는 주문 ID를 넘기면") {
            val missingOrderId = 404L

            it("IllegalArgumentException를 던진다") {
                shouldThrow<IllegalArgumentException> {
                    orderService.getOrderDetail(defaultUserId, missingOrderId)
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
                OrderCreateCommand(defaultUserId, "addr", listOf(OrderCreateCommand.Item(p1.id, 1)))
            )
            o2 = orderService.createOrder(
                OrderCreateCommand(defaultUserId, "addr", listOf(OrderCreateCommand.Item(p2.id, 1)))
            )
            o3 = orderService.createOrder(
                OrderCreateCommand(defaultUserId, "addr", listOf(OrderCreateCommand.Item(p1.id, 1)))
            )
        }

        context("첫 페이지를 size=2 로 요청하면") {
            lateinit var slice: Slice<Order>

            beforeTest { slice = orderService.getOrdersByCursor(defaultUserId, null, 2) }

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

            beforeTest { slice = orderService.getOrdersByCursor(defaultUserId, o2.id, 5) }

            it("cursor 아래의 주문(o1)만 반환하고 hasNext=false") {
                slice.content.map { it.id } shouldContainExactly listOf(o1.id)
                slice.hasNext() shouldBe false
            }
        }

        context("주문 내역이 하나도 없으면") {
            val otherUserId = 2L

            it("빈 slice 를 반환한다") {
                val slice = orderService.getOrdersByCursor(otherUserId, null, 3)
                slice.content.shouldBeEmpty()
                slice.hasNext() shouldBe false
            }
        }
    }
}) {

    private companion object {
        private const val defaultUserId = 1L
        private val categoryFixture = categoryFixture(1, "TOP")
        private val productFixture1 = productFixture(11, "A", categoryFixture, price = BigDecimal("1000"), quantity = 5)
        private val productFixture2 = productFixture(12, "B", categoryFixture, price = BigDecimal("2000"), quantity = 3)
        private val productFixture3 = productFixture(13, "C", categoryFixture, price = BigDecimal("1500"), quantity = 0)
    }
}
