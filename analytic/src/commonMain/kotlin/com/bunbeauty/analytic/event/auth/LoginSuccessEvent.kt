package com.bunbeauty.analytic.event.auth

import com.bunbeauty.analytic.event.EventParameter
import com.bunbeauty.analytic.event.FoodDeliveryEvent

class LoginSuccessEvent(direction: String) :
    FoodDeliveryEvent(
        category = "auth",
        action = "login_ok_",
        params =
            listOf(
                EventParameter(
                    key = "direction",
                    value = direction,
                ),
            ),
    )
