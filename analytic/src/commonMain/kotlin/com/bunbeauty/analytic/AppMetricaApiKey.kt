package com.bunbeauty.analytic

private val appMetricaApiKeyPattern =
    Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")

/**
 * [APP_METRICA_API_KEY] is generated from the gitignored `local.properties` entry
 * `APP_METRICA_API_KEY`. One key for every brand. An empty or non-UUID value keeps the SDK inactive.
 */
internal fun isAppMetricaApiKeyConfigured(): Boolean = appMetricaApiKeyPattern.matches(APP_METRICA_API_KEY)
