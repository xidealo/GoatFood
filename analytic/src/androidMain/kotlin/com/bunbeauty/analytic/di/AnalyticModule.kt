package com.bunbeauty.analytic.di

import com.bunbeauty.analytic.AnalyticService
import com.bunbeauty.analytic.AppMetricaAnalyticService
import com.bunbeauty.analytic.CompositeAnalyticService
import com.bunbeauty.analytic.FirebaseAnalyticService
import com.bunbeauty.core.flavorQualifier
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.dsl.module

actual fun analyticModule() =
    module {
        single<AnalyticService> {
            val eventNamePrefix = get<String>(flavorQualifier)
            CompositeAnalyticService(
                services =
                    listOf(
                        FirebaseAnalyticService(
                            firebaseAnalytics = FirebaseAnalytics.getInstance(get()),
                            eventNamePrefix = eventNamePrefix,
                        ),
                        AppMetricaAnalyticService(eventNamePrefix = eventNamePrefix),
                    ),
            )
        }
    }
