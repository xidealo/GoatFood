package com.bunbeauty.analytic

import com.bunbeauty.analytic.event.FoodDeliveryEvent

interface AnalyticService {
    fun sendEvent(event: FoodDeliveryEvent)
}
