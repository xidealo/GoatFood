package com.bunbeauty.core.domain.order

import com.bunbeauty.core.domain.repo.OrderRepo
import com.bunbeauty.core.model.order.LightOrder
import com.bunbeauty.core.model.order.OrderStatus

class GetLastDeliveredOrderUseCase(
    private val orderRepo: OrderRepo,
) {
    suspend operator fun invoke(): LightOrder? =
        orderRepo.getLastOrderByUserUuidNetworkFirst()?.takeIf { order ->
            order.status == OrderStatus.DELIVERED
        }
}
