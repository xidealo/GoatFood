package com.bunbeauty.analytic

import com.bunbeauty.analytic.event.FoodDeliveryEvent
import kotlin.test.Test
import kotlin.test.assertEquals

class CompositeAnalyticServiceTest {
    @Test
    fun sendEventDeliversTheSameEventToEveryService() {
        val firstService = RecordingAnalyticService()
        val secondService = RecordingAnalyticService()
        val composite =
            CompositeAnalyticService(
                services = listOf(firstService, secondService),
            )
        val event = FoodDeliveryEvent(category = "order", action = "create")

        composite.sendEvent(event)

        assertEquals(expected = listOf(event), actual = firstService.events)
        assertEquals(expected = listOf(event), actual = secondService.events)
    }

    @Test
    fun sendEventWithNoServicesDoesNothing() {
        val composite = CompositeAnalyticService(services = emptyList())

        composite.sendEvent(event = FoodDeliveryEvent(category = "menu", action = "open"))
    }
}

private class RecordingAnalyticService : AnalyticService {
    val events = mutableListOf<FoodDeliveryEvent>()

    override fun sendEvent(event: FoodDeliveryEvent) {
        events += event
    }
}
