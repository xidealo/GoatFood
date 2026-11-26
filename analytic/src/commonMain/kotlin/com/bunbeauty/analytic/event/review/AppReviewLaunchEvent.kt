package com.bunbeauty.analytic.event.review

import com.bunbeauty.analytic.event.EventParameter
import com.bunbeauty.analytic.event.FoodDeliveryEvent

class AppReviewLaunchEvent(
    source: String,
) : FoodDeliveryEvent(
        category = "review",
        action = "launch",
        params = listOf(EventParameter(key = "source", value = source)),
    )
