package com.bunbeauty.analytic

import com.bunbeauty.analytic.event.FoodDeliveryEvent

class CompositeAnalyticService(
    private val services: List<AnalyticService>,
) : AnalyticService {
    override fun sendEvent(event: FoodDeliveryEvent) {
        services.forEach { service ->
            service.sendEvent(event)
        }
    }
}
