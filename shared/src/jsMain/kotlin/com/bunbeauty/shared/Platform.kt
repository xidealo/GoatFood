package com.bunbeauty.shared

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.js.Js

actual val httpClientEngine: HttpClientEngine = Js.create()

actual val orderPlatform: String = "WEB"
