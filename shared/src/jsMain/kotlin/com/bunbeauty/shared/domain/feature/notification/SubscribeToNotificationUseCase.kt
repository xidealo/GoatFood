package com.bunbeauty.shared.domain.feature.notification

actual class SubscribeToNotificationUseCase {
    actual operator fun invoke(companyUuid: String, enabled: Boolean) {
        // Push notifications are not supported on the web target yet.
    }
}
