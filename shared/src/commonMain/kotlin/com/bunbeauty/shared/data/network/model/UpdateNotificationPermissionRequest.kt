package com.bunbeauty.shared.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateNotificationPermissionRequest(
    @SerialName("enabled")
    val enabled: Boolean,
)
