package com.bunbeauty.shared.data.mapper

import com.bunbeauty.core.model.Settings
import com.bunbeauty.shared.data.network.model.SettingsServer

class SettingsMapper {
    fun toSettings(settingsServer: SettingsServer): Settings =
        Settings(
            userUuid = settingsServer.userUuid,
            phoneNumber = settingsServer.phoneNumber,
            email = settingsServer.email,
            personalDiscountPercent = settingsServer.personalDiscountPercent,
            marketingNotificationsEnabled = settingsServer.marketingNotificationsEnabled,
            systemNotificationsEnabled = settingsServer.systemNotificationsEnabled,
        )
}
