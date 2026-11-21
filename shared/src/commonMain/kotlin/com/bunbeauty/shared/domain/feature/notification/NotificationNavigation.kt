package com.bunbeauty.shared.domain.feature.notification

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class NotificationOpen(
    val notificationId: String,
    val orderUuid: String,
    val campaign: String,
    val touch: String,
    val variant: String,
)

class NotificationNavigation {
    private val mutablePending = MutableStateFlow<NotificationOpen?>(null)
    val pending: StateFlow<NotificationOpen?> = mutablePending.asStateFlow()

    fun open(notification: NotificationOpen) {
        mutablePending.value = notification
    }

    fun consume(notification: NotificationOpen) {
        if (mutablePending.value == notification) {
            mutablePending.value = null
        }
    }
}
