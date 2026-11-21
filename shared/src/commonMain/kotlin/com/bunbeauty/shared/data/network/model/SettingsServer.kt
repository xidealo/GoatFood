package com.bunbeauty.shared.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SettingsServer(
    @SerialName("uuid")
    val userUuid: String,
    @SerialName("phoneNumber")
    val phoneNumber: String,
    @SerialName("email")
    val email: String?,
    @SerialName("personalDiscountPercent")
    val personalDiscountPercent: Int? = null,
    @SerialName("marketingNotificationsEnabled")
    val marketingNotificationsEnabled: Boolean = true,
    @SerialName("systemNotificationsEnabled")
    val systemNotificationsEnabled: Boolean = false,
)
