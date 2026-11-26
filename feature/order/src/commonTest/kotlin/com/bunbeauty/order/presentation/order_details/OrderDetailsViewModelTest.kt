package com.bunbeauty.order.presentation.order_details

import com.bunbeauty.core.domain.order.ObserveOrderUseCase
import com.bunbeauty.core.domain.order.StopObserveOrdersUseCase
import com.bunbeauty.core.domain.repo.OrderRepo
import com.bunbeauty.core.model.date_time.Date
import com.bunbeauty.core.model.date_time.DateTime
import com.bunbeauty.core.model.date_time.Time
import com.bunbeauty.core.model.order.Order
import com.bunbeauty.core.model.order.OrderAddress
import com.bunbeauty.core.model.order.OrderStatus
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val ORDER_UUID = "order-uuid"

@OptIn(ExperimentalCoroutinesApi::class)
class OrderDetailsViewModelTest {
    private val orderRepo = mock<OrderRepo>()
    private val viewModel =
        OrderDetailsViewModel(
            observeOrderUseCase = ObserveOrderUseCase(orderRepo = orderRepo),
            stopObserveOrdersUseCase = StopObserveOrdersUseCase(orderRepo = orderRepo),
        )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `delivered order creates app review event`() =
        runTest {
            setupOrders(order(status = OrderStatus.DELIVERED))

            viewModel.onAction(OrderDetails.Action.StartObserve(orderUuid = ORDER_UUID))

            assertEquals(
                listOf(OrderDetails.Event.RequestAppReview(orderUuid = ORDER_UUID)),
                viewModel.events.value,
            )
        }

    @Test
    fun `repeated delivered status creates only one app review event`() =
        runTest {
            val deliveredOrder = order(status = OrderStatus.DELIVERED)
            setupOrders(deliveredOrder, deliveredOrder)

            viewModel.onAction(OrderDetails.Action.StartObserve(orderUuid = ORDER_UUID))

            assertEquals(1, viewModel.events.value.count { it is OrderDetails.Event.RequestAppReview })
        }

    @Test
    fun `other order statuses do not create app review event`() =
        runTest {
            val orders =
                listOf(
                    OrderStatus.NOT_ACCEPTED,
                    OrderStatus.ACCEPTED,
                    OrderStatus.PREPARING,
                    OrderStatus.SENT_OUT,
                    OrderStatus.DONE,
                    OrderStatus.CANCELED,
                ).map(::order)
            setupOrders(*orders.toTypedArray())

            viewModel.onAction(OrderDetails.Action.StartObserve(orderUuid = ORDER_UUID))

            assertTrue(viewModel.events.value.isEmpty())
        }

    private fun setupOrders(vararg orders: Order) {
        everySuspend { orderRepo.getOrderByUuid(orderUuid = ORDER_UUID) } returns orders.first()
        everySuspend { orderRepo.observeOrderUpdates() } returns
            (null to flowOf(*orders.drop(1).toTypedArray()))
    }

    private fun order(status: OrderStatus) =
        Order(
            uuid = ORDER_UUID,
            code = "01",
            status = status,
            dateTime = DateTime(Date(1, 2, 3), Time(20, 20)),
            isDelivery = false,
            deferredTime = null,
            address =
                OrderAddress(
                    description = null,
                    street = null,
                    house = null,
                    flat = null,
                    entrance = null,
                    floor = null,
                    comment = null,
                ),
            comment = null,
            deliveryCost = null,
            orderProductList = emptyList(),
            paymentMethod = null,
            oldTotalCost = null,
            newTotalCost = 100,
            percentDiscount = null,
        )
}
