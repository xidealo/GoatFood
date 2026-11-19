package com.bunbeauty.analytic

import com.bunbeauty.analytic.event.FoodDeliveryEvent
import com.bunbeauty.analytic.event.toMap
import com.bunbeauty.core.Logger
import com.bunbeauty.core.Logger.ANALYTIC_TAG

class LogAnalyticService : AnalyticService {
    override fun sendEvent(event: FoodDeliveryEvent) {
        val name = "web_${event.category}_${event.action}"
        Logger.logD(ANALYTIC_TAG, "send event: $name with params ${event.params.toMap()}")
    }
}
