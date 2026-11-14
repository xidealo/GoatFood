package com.bunbeauty.analytic

import com.bunbeauty.analytic.event.FoodDeliveryEvent
import com.bunbeauty.analytic.event.toMap
import com.bunbeauty.core.Logger
import io.appmetrica.analytics.AppMetrica

class AppMetricaAnalyticService(
    private val eventNamePrefix: String,
) : AnalyticService {
    override fun sendEvent(event: FoodDeliveryEvent) {
        val name = event.eventName(prefix = eventNamePrefix)
        Logger.logD(Logger.ANALYTIC_TAG, "appmetrica send event: $name with params ${event.params.toMap()}")
        if (isAppMetricaApiKeyConfigured()) {
            val parameters = event.params.associate { parameter -> parameter.key to parameter.value }
            AppMetrica.reportEvent(name, parameters)
        }
    }
}
