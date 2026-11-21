package com.bunbeauty.core.domain.repo

import com.bunbeauty.core.model.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsRepo {
    suspend fun observeSettings(): Flow<Settings?>

    suspend fun getSettings(): Settings?

    suspend fun refreshSettings(): Settings?

    suspend fun updateEmail(
        email: String,
    ): Settings?

    suspend fun updateMarketingNotifications(enabled: Boolean): Settings?
}
