package com.bunbeauty.shared.domain.feature.notification

import cocoapods.FirebaseMessaging.FIRMessaging
import com.bunbeauty.core.Constants
import kotlinx.cinterop.ExperimentalForeignApi

actual class SubscribeToNotificationUseCase {
    @OptIn(ExperimentalForeignApi::class)
    actual operator fun invoke(companyUuid: String, enabled: Boolean) {
        val topic = "${Constants.NEWS_NOTIFICATION_PREFIX}$companyUuid"
        if (enabled) {
            FIRMessaging.messaging().subscribeToTopic(topic)
        } else {
            FIRMessaging.messaging().unsubscribeFromTopic(topic)
        }
    }
}
