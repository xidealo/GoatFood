package com.bunbeauty.shared.domain.feature.notification

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NotificationNavigationTest {

    @Test
    fun `pending notification is consumed once`() {
        val navigation = NotificationNavigation()
        val notification = NotificationOpen(
            notificationId = "notification",
            orderUuid = "order",
            campaign = "client_return",
            touch = "1",
            variant = "PERSONAL",
        )

        navigation.open(notification)
        assertEquals(notification, navigation.pending.value)

        navigation.consume(notification)
        assertNull(navigation.pending.value)

        navigation.consume(notification)
        assertNull(navigation.pending.value)
    }
}
