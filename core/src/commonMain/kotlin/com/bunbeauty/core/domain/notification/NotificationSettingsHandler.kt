package com.bunbeauty.core.domain.notification

interface NotificationSettingsHandler {
    fun requestPermission()
    fun setNewsSubscription(enabled: Boolean)
}
