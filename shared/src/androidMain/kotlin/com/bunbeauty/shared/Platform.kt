package com.bunbeauty.shared

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

actual val httpClientEngine: HttpClientEngine = OkHttp.create()

actual val orderPlatform: String = "ANDROID"
