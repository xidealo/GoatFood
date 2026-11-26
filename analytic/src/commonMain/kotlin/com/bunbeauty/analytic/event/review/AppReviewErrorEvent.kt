package com.bunbeauty.analytic.event.review

import com.bunbeauty.analytic.event.EventParameter
import com.bunbeauty.analytic.event.FoodDeliveryEvent

class AppReviewErrorEvent(
    source: String,
    stage: String,
    error: String,
) : FoodDeliveryEvent(
        category = "review",
        action = "error",
        params =
            listOf(
                EventParameter(key = "source", value = source),
                EventParameter(key = "stage", value = stage),
                EventParameter(key = "error", value = error),
            ),
    )
