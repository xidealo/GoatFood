package com.bunbeauty.shared.data.repository

import com.bunbeauty.core.domain.repo.SettingsRepo
import com.bunbeauty.core.model.Settings
import com.bunbeauty.shared.DataStoreRepo
import com.bunbeauty.shared.data.mapper.SettingsMapper
import com.bunbeauty.shared.data.network.api.NetworkConnector
import com.bunbeauty.shared.data.network.model.profile.patch.PatchUserServer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SettingsRepository(
    private val dataStoreRepo: DataStoreRepo,
    private val networkConnector: NetworkConnector,
    private val settingsMapper: SettingsMapper,
) : BaseRepository(),
    SettingsRepo {
    override val tag: String = "SETTINGS_TAG"

    override suspend fun observeSettings(): Flow<Settings?> {
        val token = dataStoreRepo.getToken() ?: return flow { emit(null) }

        networkConnector.getSettings(token).getNullableResult { settingsServer ->
            dataStoreRepo.saveSettings(settingsMapper.toSettings(settingsServer))
        }
        return dataStoreRepo.settings
    }

    override suspend fun getSettings(): Settings? {
        val localSettings = dataStoreRepo.getSettings()
        if (localSettings != null && localSettings.userUuid.isNotEmpty()) {
            return localSettings
        }
        return refreshSettings()
    }

    override suspend fun refreshSettings(): Settings? {
        val token = dataStoreRepo.getToken() ?: return dataStoreRepo.getSettings()
        return networkConnector.getSettings(token).getNullableResult(
            onError = {
                dataStoreRepo.getSettings()
            },
            onSuccess = { settingsServer ->
                val settings = settingsMapper.toSettings(settingsServer)
                dataStoreRepo.saveSettings(settings)
                settings
            },
        )
    }

    override suspend fun updateMarketingNotifications(enabled: Boolean): Settings? {
        val token = dataStoreRepo.getToken() ?: return null
        return networkConnector
            .patchSettings(token, PatchUserServer(marketingNotificationsEnabled = enabled))
            .getNullableResult { settingsServer ->
                val settings = settingsMapper.toSettings(settingsServer)
                dataStoreRepo.saveSettings(settings)
                settings
            }
    }

    override suspend fun updateEmail(email: String): Settings? {
        val token = dataStoreRepo.getToken() ?: return null
        return networkConnector
            .patchSettings(token, PatchUserServer(email = email))
            .getNullableResult { settingsServer ->
                val settings = settingsMapper.toSettings(settingsServer)
                dataStoreRepo.saveSettings(settings)
                settings
            }
    }
}
