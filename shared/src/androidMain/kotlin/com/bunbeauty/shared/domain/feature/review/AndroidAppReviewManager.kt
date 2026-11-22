package com.bunbeauty.shared.domain.feature.review

import android.app.Activity
import android.content.Context
import com.bunbeauty.analytic.AnalyticService
import com.bunbeauty.analytic.event.review.AppReviewErrorEvent
import com.bunbeauty.analytic.event.review.AppReviewFinishEvent
import com.bunbeauty.analytic.event.review.AppReviewLaunchEvent
import com.bunbeauty.analytic.event.review.AppReviewRequestEvent
import com.bunbeauty.core.Logger
import com.bunbeauty.core.domain.review.AppReviewManager
import com.bunbeauty.core.domain.review.AppReviewSource
import com.google.android.play.core.review.ReviewException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val APP_REVIEW_LOG_TAG = "AppReviewManager"
private const val REQUEST_STAGE = "request"
private const val LAUNCH_STAGE = "launch"
private const val UNKNOWN_ERROR = "unknown"
private const val REVIEW_COOLDOWN_MILLIS = 90L * 24 * 60 * 60 * 1000

class AndroidAppReviewManager internal constructor(
    private val analyticService: AnalyticService,
    private val storage: AppReviewStorage,
    private val reviewFlow: AppReviewFlow,
    private val currentTimeMillis: () -> Long,
) : AppReviewManager {
    constructor(
        context: Context,
        analyticService: AnalyticService,
    ) : this(
        analyticService = analyticService,
        storage = DataStoreAppReviewStorage(context = context),
        reviewFlow = PlayAppReviewFlow(context = context),
        currentTimeMillis = System::currentTimeMillis,
    )

    private var activity: Activity? = null
    private val requestMutex = Mutex()

    fun register(activity: Activity) {
        this.activity = activity
    }

    fun unregister(activity: Activity) {
        if (this.activity === activity) {
            this.activity = null
        }
    }

    override suspend fun request(
        orderUuid: String,
        source: AppReviewSource,
    ) {
        requestMutex.withLock {
            val currentActivity = activity ?: return
            if (!shouldRequest(orderUuid = orderUuid)) {
                return
            }

            saveRequest(orderUuid = orderUuid)
            val analyticSource = source.toAnalyticValue()
            analyticService.sendEvent(AppReviewRequestEvent(source = analyticSource))

            val reviewSession =
                try {
                    reviewFlow.prepare()
                } catch (throwable: Throwable) {
                    handleError(throwable = throwable, stage = REQUEST_STAGE, source = analyticSource)
                    return
                }

            analyticService.sendEvent(AppReviewLaunchEvent(source = analyticSource))
            try {
                reviewSession.launch(activity = currentActivity)
                analyticService.sendEvent(AppReviewFinishEvent(source = analyticSource))
            } catch (throwable: Throwable) {
                handleError(throwable = throwable, stage = LAUNCH_STAGE, source = analyticSource)
            }
        }
    }

    private suspend fun shouldRequest(orderUuid: String): Boolean {
        val lastRequest = storage.getLastRequest() ?: return true
        if (lastRequest.orderUuid == orderUuid) {
            return false
        }
        return currentTimeMillis() - lastRequest.timestamp >= REVIEW_COOLDOWN_MILLIS
    }

    private suspend fun saveRequest(orderUuid: String) {
        storage.saveRequest(
            StoredAppReviewRequest(
                orderUuid = orderUuid,
                timestamp = currentTimeMillis(),
            ),
        )
    }

    private fun handleError(
        throwable: Throwable,
        stage: String,
        source: String,
    ) {
        Logger.logE(APP_REVIEW_LOG_TAG, throwable.stackTraceToString())
        analyticService.sendEvent(
            AppReviewErrorEvent(
                source = source,
                stage = stage,
                error = throwable.toAnalyticError(),
            ),
        )
    }

    private fun Throwable.toAnalyticError(): String =
        when (this) {
            is ReviewException -> errorCode.toString()
            else -> this::class.simpleName ?: UNKNOWN_ERROR
        }

    private fun AppReviewSource.toAnalyticValue(): String = name.lowercase()
}
