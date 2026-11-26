package com.bunbeauty.shared.domain.feature.review

import android.app.Activity
import com.bunbeauty.analytic.AnalyticService
import com.bunbeauty.analytic.event.FoodDeliveryEvent
import com.bunbeauty.core.domain.review.AppReviewSource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

private const val ORDER_UUID = "order-uuid"
private const val NEW_ORDER_UUID = "new-order-uuid"
private const val COOLDOWN_MILLIS = 90L * 24 * 60 * 60 * 1000

class AndroidAppReviewManagerTest {
    private val activity = Activity()
    private val storage = FakeAppReviewStorage()
    private val reviewFlow = TestReviewFlow()
    private val analyticService = RecordingAnalyticService()
    private var currentTimeMillis = 1_000_000L
    private val appReviewManager =
        AndroidAppReviewManager(
            analyticService = analyticService,
            storage = storage,
            reviewFlow = reviewFlow,
            currentTimeMillis = { currentTimeMillis },
        )

    @Test
    fun `first request launches review and sends analytics`() =
        runTest {
            appReviewManager.register(activity)

            appReviewManager.request(orderUuid = ORDER_UUID, source = AppReviewSource.MENU)

            assertEquals(1, reviewFlow.requestCount)
            assertEquals(1, reviewFlow.session.launchCount)
            assertEquals(listOf("request", "launch", "finish"), analyticService.events.map { it.action })
            assertEquals("menu", analyticService.events.first().parameterValue(key = "source"))
        }

    @Test
    fun `same order and cooldown do not launch review again`() =
        runTest {
            appReviewManager.register(activity)

            appReviewManager.request(orderUuid = ORDER_UUID, source = AppReviewSource.MENU)
            appReviewManager.request(orderUuid = ORDER_UUID, source = AppReviewSource.ORDER_DETAILS)
            appReviewManager.request(orderUuid = NEW_ORDER_UUID, source = AppReviewSource.MENU)

            assertEquals(1, reviewFlow.requestCount)
            assertEquals(1, reviewFlow.session.launchCount)
        }

    @Test
    fun `new order launches review after cooldown`() =
        runTest {
            appReviewManager.register(activity)

            appReviewManager.request(orderUuid = ORDER_UUID, source = AppReviewSource.MENU)
            currentTimeMillis += COOLDOWN_MILLIS
            appReviewManager.request(orderUuid = NEW_ORDER_UUID, source = AppReviewSource.ORDER_DETAILS)

            assertEquals(2, reviewFlow.requestCount)
            assertEquals(2, reviewFlow.session.launchCount)
            assertEquals("order_details", analyticService.events.last().parameterValue(key = "source"))
        }

    @Test
    fun `parallel requests launch review only once`() =
        runTest {
            appReviewManager.register(activity)

            listOf(
                async { appReviewManager.request(orderUuid = ORDER_UUID, source = AppReviewSource.MENU) },
                async {
                    appReviewManager.request(
                        orderUuid = ORDER_UUID,
                        source = AppReviewSource.ORDER_DETAILS,
                    )
                },
            ).awaitAll()

            assertEquals(1, reviewFlow.requestCount)
            assertEquals(1, reviewFlow.session.launchCount)
        }

    @Test
    fun `request error sends analytics without launch`() =
        runTest {
            appReviewManager.register(activity)
            reviewFlow.requestError = IllegalStateException("Request failed")

            appReviewManager.request(orderUuid = ORDER_UUID, source = AppReviewSource.MENU)

            assertEquals(1, reviewFlow.requestCount)
            assertEquals(0, reviewFlow.session.launchCount)
            assertEquals(listOf("request", "error"), analyticService.events.map { it.action })
            assertEquals("request", analyticService.events.last().parameterValue(key = "stage"))
            assertEquals("IllegalStateException", analyticService.events.last().parameterValue(key = "error"))
        }

    @Test
    fun `launch error sends analytics`() =
        runTest {
            appReviewManager.register(activity)
            reviewFlow.session.launchError = IllegalStateException("Launch failed")

            appReviewManager.request(orderUuid = ORDER_UUID, source = AppReviewSource.MENU)

            assertEquals(1, reviewFlow.session.launchCount)
            assertEquals(listOf("request", "launch", "error"), analyticService.events.map { it.action })
            assertEquals("launch", analyticService.events.last().parameterValue(key = "stage"))
        }

    private fun FoodDeliveryEvent.parameterValue(key: String) = params.first { parameter -> parameter.key == key }.value
}

private class FakeAppReviewStorage : AppReviewStorage {
    private var lastRequest: StoredAppReviewRequest? = null

    override suspend fun getLastRequest(): StoredAppReviewRequest? = lastRequest

    override suspend fun saveRequest(request: StoredAppReviewRequest) {
        lastRequest = request
    }
}

private class TestReviewFlow : AppReviewFlow {
    val session = TestReviewSession()
    var requestCount = 0
        private set
    var requestError: Exception? = null

    override suspend fun prepare(): AppReviewSession {
        requestCount++
        requestError?.let { throw it }
        return session
    }
}

private class TestReviewSession : AppReviewSession {
    var launchCount = 0
        private set
    var launchError: Exception? = null

    override suspend fun launch(activity: Activity) {
        launchCount++
        launchError?.let { throw it }
    }
}

private class RecordingAnalyticService : AnalyticService {
    val events = mutableListOf<FoodDeliveryEvent>()

    override fun sendEvent(event: FoodDeliveryEvent) {
        events += event
    }
}
