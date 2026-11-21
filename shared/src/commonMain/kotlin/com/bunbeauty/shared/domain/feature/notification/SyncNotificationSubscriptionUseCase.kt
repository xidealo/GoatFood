package com.bunbeauty.shared.domain.feature.notification

import com.bunbeauty.core.domain.repo.SettingsRepo
import com.bunbeauty.shared.data.CompanyUuidProvider

class SyncNotificationSubscriptionUseCase(
    private val settingsRepository: SettingsRepo,
    private val subscribeToNotificationUseCase: SubscribeToNotificationUseCase,
    private val companyUuidProvider: CompanyUuidProvider,
) {
    suspend operator fun invoke() {
        subscribeToNotificationUseCase(
            companyUuid = companyUuidProvider.companyUuid,
            enabled = settingsRepository.getSettings()?.marketingNotificationsEnabled == true,
        )
    }
}
