package com.bunbeauty.analytic

import android.os.Bundle
import com.bunbeauty.analytic.event.EventParameter
import com.bunbeauty.analytic.event.FoodDeliveryEvent
import com.bunbeauty.core.Logger
import com.google.firebase.analytics.FirebaseAnalytics

class FirebaseAnalyticService(
    private val firebaseAnalytics: FirebaseAnalytics,
    private val eventNamePrefix: String,
) : AnalyticService {
    override fun sendEvent(event: FoodDeliveryEvent) {
        val name = event.eventName(prefix = eventNamePrefix)
        val parameters = event.params.toBundle()
        Logger.logD(Logger.ANALYTIC_TAG, "firebase send event: $name with params $parameters")
        firebaseAnalytics.logEvent(name, parameters)
    }

    private fun List<EventParameter>.toBundle(): Bundle =
        Bundle(size).apply {
            forEach { parameter ->
                putString(parameter.key, parameter.value)
            }
        }
}
