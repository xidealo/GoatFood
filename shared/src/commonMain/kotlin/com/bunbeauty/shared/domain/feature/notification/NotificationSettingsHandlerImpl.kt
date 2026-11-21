package com.bunbeauty.shared.domain.feature.notification

import com.bunbeauty.core.domain.notification.NotificationSettingsHandler
import com.bunbeauty.shared.data.CompanyUuidProvider

class NotificationSettingsHandlerImpl(
    private val notificationPermissionRequester: NotificationPermissionRequester,
    private val subscribeToNotificationUseCase: SubscribeToNotificationUseCase,
    private val companyUuidProvider: CompanyUuidProvider,
) : NotificationSettingsHandler {
    override fun requestPermission() {
        notificationPermissionRequester.request()
    }

    override fun setNewsSubscription(enabled: Boolean) {
        subscribeToNotificationUseCase(
            companyUuid = companyUuidProvider.companyUuid,
            enabled = enabled,
        )
    }
}
