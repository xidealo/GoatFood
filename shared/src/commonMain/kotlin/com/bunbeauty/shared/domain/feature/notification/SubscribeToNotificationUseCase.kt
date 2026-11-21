package com.bunbeauty.shared.domain.feature.notification

expect class SubscribeToNotificationUseCase() {
    operator fun invoke(companyUuid: String, enabled: Boolean)
}
