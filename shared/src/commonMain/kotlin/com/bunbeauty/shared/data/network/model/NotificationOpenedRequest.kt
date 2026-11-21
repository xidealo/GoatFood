package com.bunbeauty.shared.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationOpenedRequest(
    @SerialName("notificationId")
    val notificationId: String,
)
