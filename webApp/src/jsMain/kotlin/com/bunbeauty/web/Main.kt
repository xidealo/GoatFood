package com.bunbeauty.web

import androidx.compose.runtime.SideEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.window.ComposeViewport
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.map.Mapper
import coil3.memory.MemoryCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.CachePolicy
import coil3.request.Options
import coil3.request.crossfade
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import com.bunbeauty.designsystem.theme.FoodDeliveryTheme
import com.bunbeauty.shared.data.createFoodDeliveryDatabase
import com.bunbeauty.shared.data.network.NetworkConfig
import com.bunbeauty.shared.db.FoodDeliveryDatabase
import com.bunbeauty.shared.di.initKoin
import com.bunbeauty.shared.resolveWebFlavor
import com.bunbeauty.shared.ui.screen.main.MainScreen
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.dsl.module
import org.w3c.dom.Worker


private fun applyThemeColor(color: Color) {
    val rgb = color.toArgb() and 0xFFFFFF
    val hex = "#" + rgb.toString(16).padStart(6, '0').uppercase()
    val meta =
        document.querySelector("meta[name='theme-color']")
            ?: document.createElement("meta").also { element ->
                element.setAttribute("name", "theme-color")
                document.head?.appendChild(element)
            }
    meta.setAttribute("content", hex)
}

private fun applyFavicon(flavor: String) {
    val href = "composeResources/papakarlo.designsystem.generated.resources/drawable/logo_small_$flavor.webp"
    val link =
        document.querySelector("link[rel='icon']")
            ?: document.createElement("link").also { element ->
                element.setAttribute("rel", "icon")
                document.head?.appendChild(element)
            }
    link.setAttribute("type", "image/webp")
    link.setAttribute("href", href)
}

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // The backend (amvera) does not send CORS headers, so on the web we route
    // requests through the dev-server proxy by talking to the current page origin.
    // The webpack proxy (webApp/webpack.config.d/proxy.js) forwards them to amvera.
    NetworkConfig.protocol = window.location.protocol.trimEnd(':')
    NetworkConfig.host = window.location.hostname
    NetworkConfig.port = window.location.port.toIntOrNull()

    // One build/host serves many cafes: the brand is picked from the domain.
    // Must match the flavor Koin resolves in PlatformModule (also resolveWebFlavor).
    val flavor = resolveWebFlavor()

    // index.html has no favicon, and a host may still serve an older copy of it
    // with a stale title, so both are set here at runtime.
    document.title = "Доставка еды"
    applyFavicon(flavor)

    CoroutineScope(Dispatchers.Main).launch {
        val driver = WebWorkerDriver(
            Worker(
                js("""new URL("@cashapp/sqldelight-sqljs-worker/sqljs.worker.js", import.meta.url)"""),
            ),
        )
        FoodDeliveryDatabase.Schema.create(driver).await()
        val database = createFoodDeliveryDatabase(driver)

        initKoin {
            modules(
                module {
                    single { database }
                },
            )
        }

        ComposeViewport(document.body!!) {
            // On the web there is no real filesystem, so we use a memory-only
            // ImageLoader. Coil's default loader would try to set up a disk cache
            // via okio (os.tmpdir()), which is not available in the browser.
            setSingletonImageLoaderFactory { context ->
                ImageLoader
                    .Builder(context)
                    // On JS Coil does not auto-register a network fetcher (no
                    // ServiceLoader), so we add the Ktor one explicitly. Without it
                    // every http(s) image fails and only the placeholder is shown.
                    // Firebase photos: on localhost we proxy via webpack (Firebase has
                    // no CORS). On Amvera and other production hosts the server cannot
                    // reach Google, so the browser loads Firebase URLs directly — the
                    // bucket must allow CORS (see webApp/firebase-storage-cors.json).
                    .components {
                        if (shouldUseFirebaseImageProxy()) {
                            add(FirebaseImageProxyMapper())
                        }
                        add(YandexImageProxyMapper())
                        add(KtorNetworkFetcherFactory())
                    }
                    // There is no disk cache on the web, so we keep a generous
                    // in-memory cache. Otherwise images get evicted quickly and
                    // reload every time they scroll back into view.
                    .memoryCache {
                        MemoryCache
                            .Builder()
                            .maxSizeBytes(256L * 1024 * 1024)
                            .build()
                    }
                    .diskCachePolicy(CachePolicy.DISABLED)
                    .crossfade(enable = true)
                    .build()
            }

            FoodDeliveryTheme(flavor = flavor) {
                val themeColor = FoodDeliveryTheme.colors.mainColors.primary
                SideEffect {
                    applyThemeColor(themeColor)
                }
                MainScreen()
            }
        }
    }
}

// Same-origin /firebase-img proxy only works where the host can reach Firebase
// (local webpack dev-server). Amvera and custom production domains load Firebase
// URLs directly in the browser instead.
private fun shouldUseFirebaseImageProxy(): Boolean {
    val host = window.location.hostname.lowercase()
    return host == "localhost" || host == "127.0.0.1"
}

private const val FIREBASE_STORAGE_URL = "https://firebasestorage.googleapis.com"

// Rewrites Firebase Storage photo URLs to the same-origin /firebase-img proxy
// path so the browser doesn't block them with CORS. The dev-server proxy
// (webApp/webpack.config.d/proxy.js) forwards these to Firebase Storage.
private class FirebaseImageProxyMapper : Mapper<String, String> {
    override fun map(data: String, options: Options): String? =
        if (data.startsWith(FIREBASE_STORAGE_URL)) {
            window.location.origin + "/firebase-img" + data.removePrefix(FIREBASE_STORAGE_URL)
        } else {
            null
        }
}

private const val YANDEX_STORAGE_URL = "https://fooddelivery-s3-test.storage.yandexcloud.net"

// Some product photos live on Yandex Object Storage, which (like Firebase) does
// not send CORS headers, so direct browser fetches are blocked. We rewrite those
// URLs to the same-origin /yandex-img proxy path, forwarded to Yandex Storage by
// the dev-server proxy (locally) and netlify.toml (in production).
private class YandexImageProxyMapper : Mapper<String, String> {
    override fun map(data: String, options: Options): String? =
        if (data.startsWith(YANDEX_STORAGE_URL)) {
            window.location.origin + "/yandex-img" + data.removePrefix(YANDEX_STORAGE_URL)
        } else {
            null
        }
}
