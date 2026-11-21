package com.bunbeauty.shared.domain.feature.notification

actual class NotificationPermissionRequester {
    private var requestBlock: (() -> Unit)? = null

    actual fun request() {
        requestBlock?.invoke()
    }

    fun register(requestBlock: () -> Unit) {
        this.requestBlock = requestBlock
    }

    fun unregister() {
        requestBlock = null
    }
}
