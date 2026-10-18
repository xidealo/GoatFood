package com.bunbeauty.analytic.event.auth

import com.bunbeauty.analytic.event.EventParameter
import com.bunbeauty.analytic.event.FoodDeliveryEvent

class AuthConfirmErrorEvent(error: String) :
    FoodDeliveryEvent(
        category = "auth",
        action = "confirm_err",
        params =
            listOf(
                EventParameter(
                    key = "error",
                    value = error,
                ),
            ),
    )

