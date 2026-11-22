package com.bunbeauty.domain.feature.order

import com.bunbeauty.core.domain.order.GetLastDeliveredOrderUseCase
import com.bunbeauty.core.domain.repo.OrderRepo
import com.bunbeauty.core.model.date_time.Date
import com.bunbeauty.core.model.date_time.DateTime
import com.bunbeauty.core.model.date_time.Time
import com.bunbeauty.core.model.order.LightOrder
import com.bunbeauty.core.model.order.OrderStatus
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetLastDeliveredOrderUseCaseTest {
    private val orderRepo: OrderRepo = mock()
    private val getLastDeliveredOrderUseCase = GetLastDeliveredOrderUseCase(orderRepo = orderRepo)

    @Test
    fun `returns null when repository has no last order`() =
        runTest {
            everySuspend { orderRepo.getLastOrderByUserUuidNetworkFirst() } returns null

            assertNull(getLastDeliveredOrderUseCase())
        }

    @Test
    fun `returns last order when it is delivered`() =
        runTest {
            val order = lightOrder(status = OrderStatus.DELIVERED)
            everySuspend { orderRepo.getLastOrderByUserUuidNetworkFirst() } returns order

            assertEquals(order, getLastDeliveredOrderUseCase())
        }

    @Test
    fun `returns null when last order is not completed`() =
        runTest {
            everySuspend { orderRepo.getLastOrderByUserUuidNetworkFirst() } returns
                lightOrder(status = OrderStatus.PREPARING)

            assertNull(getLastDeliveredOrderUseCase())
        }

    @Test
    fun `returns null when last order is done`() =
        runTest {
            everySuspend { orderRepo.getLastOrderByUserUuidNetworkFirst() } returns
                lightOrder(status = OrderStatus.DONE)

            assertNull(getLastDeliveredOrderUseCase())
        }

    @Test
    fun `returns null when last order is canceled`() =
        runTest {
            everySuspend { orderRepo.getLastOrderByUserUuidNetworkFirst() } returns
                lightOrder(status = OrderStatus.CANCELED)

            assertNull(getLastDeliveredOrderUseCase())
        }

    private fun lightOrder(status: OrderStatus) =
        LightOrder(
            uuid = "order-uuid",
            status = status,
            code = "01",
            dateTime = DateTime(Date(1, 2, 3), Time(20, 20)),
        )
}
