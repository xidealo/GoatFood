package com.bunbeauty.analytic.di

import com.bunbeauty.analytic.AnalyticService
import com.bunbeauty.analytic.CompositeAnalyticService
import com.bunbeauty.analytic.LogAnalyticService
import org.koin.dsl.module

actual fun analyticModule() =
    module {
        single<AnalyticService> {
            CompositeAnalyticService(
                services = listOf(LogAnalyticService()),
            )
        }
    }
