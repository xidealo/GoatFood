package com.bunbeauty.shared

import io.ktor.client.engine.HttpClientEngine

expect val httpClientEngine: HttpClientEngine

expect val orderPlatform: String
