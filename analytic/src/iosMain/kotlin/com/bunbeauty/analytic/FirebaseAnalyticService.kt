@file:OptIn(ExperimentalForeignApi::class)

package com.bunbeauty.analytic

import cocoapods.FirebaseAnalytics.FIRAnalytics
import com.bunbeauty.analytic.event.FoodDeliveryEvent
import com.bunbeauty.analytic.event.toMap
import com.bunbeauty.core.Logger
import com.bunbeauty.core.Logger.ANALYTIC_TAG
import kotlinx.cinterop.ExperimentalForeignApi

class FirebaseAnalyticService(
    private val eventNamePrefix: String,
) : AnalyticService {
    override fun sendEvent(event: FoodDeliveryEvent) {
        val name = event.eventName(prefix = eventNamePrefix)
        Logger.logD(ANALYTIC_TAG, "firebase send event: $name with params ${event.params.toMap()}")
        FIRAnalytics.logEventWithName(name, parameters = event.params.toNativeParameters())
    }
}
