@file:OptIn(ExperimentalForeignApi::class)

package com.bunbeauty.analytic

import cocoapods.AppMetricaCore.AMAAppMetrica
import com.bunbeauty.analytic.event.FoodDeliveryEvent
import com.bunbeauty.analytic.event.toMap
import com.bunbeauty.core.Logger
import com.bunbeauty.core.Logger.ANALYTIC_TAG
import kotlinx.cinterop.ExperimentalForeignApi

class AppMetricaAnalyticService(
    private val eventNamePrefix: String,
) : AnalyticService {
    override fun sendEvent(event: FoodDeliveryEvent) {
        val name = event.eventName(prefix = eventNamePrefix)
        Logger.logD(ANALYTIC_TAG, "appmetrica send event: $name with params ${event.params.toMap()}")
        if (isAppMetricaApiKeyConfigured()) {
            AMAAppMetrica.reportEvent(
                name,
                parameters = event.params.toNativeParameters(),
                onFailure = { error ->
                    Logger.logE(ANALYTIC_TAG, "appmetrica report failed: $error")
                },
            )
        }
    }
}
