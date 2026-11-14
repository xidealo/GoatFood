package com.bunbeauty.analytic.di

import com.bunbeauty.analytic.AnalyticService
import com.bunbeauty.analytic.AppMetricaAnalyticService
import com.bunbeauty.analytic.CompositeAnalyticService
import com.bunbeauty.analytic.FirebaseAnalyticService
import com.bunbeauty.core.targetName
import org.koin.dsl.module

actual fun analyticModule() =
    module {
        single<AnalyticService> {
            CompositeAnalyticService(
                services =
                    listOf(
                        FirebaseAnalyticService(eventNamePrefix = targetName),
                        AppMetricaAnalyticService(eventNamePrefix = targetName),
                    ),
            )
        }
    }
