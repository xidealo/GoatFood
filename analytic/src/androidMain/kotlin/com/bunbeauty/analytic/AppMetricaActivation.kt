package com.bunbeauty.analytic

import android.content.Context
import com.bunbeauty.core.Logger
import io.appmetrica.analytics.AppMetrica
import io.appmetrica.analytics.AppMetricaConfig

fun activateAppMetrica(
    context: Context,
    dataSendingEnabled: Boolean,
) {
    if (isAppMetricaApiKeyConfigured()) {
        val config =
            AppMetricaConfig
                .newConfigBuilder(APP_METRICA_API_KEY)
                .withDataSendingEnabled(dataSendingEnabled)
                .build()
        AppMetrica.activate(context, config)
        Logger.logD(
            Logger.ANALYTIC_TAG,
            "AppMetrica activated, dataSendingEnabled=$dataSendingEnabled",
        )
    } else {
        Logger.logD(Logger.ANALYTIC_TAG, "AppMetrica activation skipped: API key is not set")
    }
}
