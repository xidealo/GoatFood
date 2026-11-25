package com.bunbeauty.analytic.event.review

import com.bunbeauty.analytic.event.EventParameter
import com.bunbeauty.analytic.event.FoodDeliveryEvent

class AppReviewRequestEvent(
    source: String,
) : FoodDeliveryEvent(
        category = "review",
        action = "request",
        params = listOf(EventParameter(key = "source", value = source)),
    )
