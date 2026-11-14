@file:OptIn(ExperimentalForeignApi::class)

package com.bunbeauty.analytic

import cocoapods.AppMetricaCore.AMAAppMetrica
import cocoapods.AppMetricaCore.AMAAppMetricaConfiguration
import com.bunbeauty.core.Logger
import com.bunbeauty.core.Logger.ANALYTIC_TAG
import kotlinx.cinterop.ExperimentalForeignApi

fun activateAppMetrica(dataSendingEnabled: Boolean) {
    val configuration = appMetricaConfiguration()
    if (configuration != null) {
        configuration.dataSendingEnabled = dataSendingEnabled
        AMAAppMetrica.activateWithConfiguration(configuration)
        Logger.logD(ANALYTIC_TAG, "AppMetrica activated, dataSendingEnabled=$dataSendingEnabled")
    } else {
        Logger.logD(ANALYTIC_TAG, "AppMetrica activation skipped: API key is not set")
    }
}

private fun appMetricaConfiguration(): AMAAppMetricaConfiguration? {
    if (isAppMetricaApiKeyConfigured()) {
        return AMAAppMetricaConfiguration(APIKey = APP_METRICA_API_KEY)
    }
    return null
}
