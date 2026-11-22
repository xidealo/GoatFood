package com.bunbeauty.shared.domain.feature.review

import android.app.Activity
import android.content.Context
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal interface AppReviewFlow {
    suspend fun prepare(): AppReviewSession
}

internal interface AppReviewSession {
    suspend fun launch(activity: Activity)
}

internal class PlayAppReviewFlow(
    context: Context,
) : AppReviewFlow {
    private val reviewManager = ReviewManagerFactory.create(context)

    override suspend fun prepare(): AppReviewSession {
        val reviewInfo = requestReviewInfo()
        return PlayAppReviewSession(reviewManager = reviewManager, reviewInfo = reviewInfo)
    }

    private suspend fun requestReviewInfo(): ReviewInfo =
        suspendCancellableCoroutine { continuation ->
            reviewManager.requestReviewFlow().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(task.result)
                } else {
                    continuation.resumeWithException(
                        task.exception ?: IllegalStateException("Review request failed"),
                    )
                }
            }
        }
}

private class PlayAppReviewSession(
    private val reviewManager: ReviewManager,
    private val reviewInfo: ReviewInfo,
) : AppReviewSession {
    override suspend fun launch(activity: Activity) =
        suspendCancellableCoroutine { continuation ->
            reviewManager.launchReviewFlow(activity, reviewInfo).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(Unit)
                } else {
                    continuation.resumeWithException(
                        task.exception ?: IllegalStateException("Review launch failed"),
                    )
                }
            }
        }
}
