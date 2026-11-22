package com.bunbeauty.analytic.event.review

import com.bunbeauty.analytic.event.EventParameter
import com.bunbeauty.analytic.event.FoodDeliveryEvent

class AppReviewFinishEvent(
    source: String,
) : FoodDeliveryEvent(
        category = "review",
        action = "finish",
        params = listOf(EventParameter(key = "source", value = source)),
    )
