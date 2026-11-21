package com.bunbeauty.analytic.event.notification

import com.bunbeauty.analytic.event.EventParameter
import com.bunbeauty.analytic.event.FoodDeliveryEvent

class NotificationOpenEvent(
    campaign: String,
    touch: String,
    variant: String,
) : FoodDeliveryEvent(
    category = "notification",
    action = "open",
    params = listOf(
        EventParameter(key = "campaign", value = campaign),
        EventParameter(key = "touch", value = touch),
        EventParameter(key = "variant", value = variant),
    ),
)
