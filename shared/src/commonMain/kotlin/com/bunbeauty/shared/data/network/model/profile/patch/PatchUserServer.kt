package com.bunbeauty.shared.data.network.model.profile.patch

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PatchUserServer(
    @SerialName("email")
    val email: String? = null,
    @SerialName("isActive")
    val isActive: Boolean? = null,
    @SerialName("marketingNotificationsEnabled")
    val marketingNotificationsEnabled: Boolean? = null,
)
