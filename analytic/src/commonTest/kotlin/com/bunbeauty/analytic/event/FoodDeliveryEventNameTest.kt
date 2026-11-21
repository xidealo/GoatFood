package com.bunbeauty.analytic.event

import com.bunbeauty.analytic.event.auth.AuthConfirmErrorEvent
import com.bunbeauty.analytic.event.auth.LoginRequestEvent
import com.bunbeauty.analytic.event.auth.LoginSuccessEvent
import com.bunbeauty.analytic.event.cart.AddCartProductDetailsClickEvent
import com.bunbeauty.analytic.event.cart.CartProceedClickEvent
import com.bunbeauty.analytic.event.cart.DecreaseCartProductClickEvent
import com.bunbeauty.analytic.event.cart.IncreaseCartProductClickEvent
import com.bunbeauty.analytic.event.cart.RemoveCartProductClickEvent
import com.bunbeauty.analytic.event.city.CitySelectEvent
import com.bunbeauty.analytic.event.favorite.AddFavoriteClickEvent
import com.bunbeauty.analytic.event.favorite.RemoveFavoriteClickEvent
import com.bunbeauty.analytic.event.menu.AddMenuProductClickEvent
import com.bunbeauty.analytic.event.menu.AddMenuProductDetailsClickEvent
import com.bunbeauty.analytic.event.menu.LoadedMenuEvent
import com.bunbeauty.analytic.event.notification.NotificationOpenEvent
import com.bunbeauty.analytic.event.order.OrderCreateClickEvent
import com.bunbeauty.analytic.event.order.OrderCreateFailEvent
import com.bunbeauty.analytic.event.order.OrderCreateSuccessEvent
import com.bunbeauty.analytic.event.order.OrderDeliveryToggleEvent
import com.bunbeauty.analytic.event.recommendation.AddRecommendationProductClickEvent
import com.bunbeauty.analytic.event.recommendation.AddRecommendationProductDetailsClickEvent
import com.bunbeauty.analytic.parameter.MenuProductUuidEventParameter
import com.bunbeauty.analytic.parameter.TimeParameter
import kotlin.test.Test
import kotlin.test.assertTrue

class FoodDeliveryEventNameTest {
    @Test
    fun allEventNamesAreWithinFirebaseLimit() {
        val events =
            listOf(
                LoadedMenuEvent(timeParameter = TimeParameter(value = "100")),
                AddMenuProductClickEvent(
                    menuProductUuidEventParameter = MenuProductUuidEventParameter(value = "uuid"),
                ),
                AddMenuProductDetailsClickEvent(
                    menuProductUuidEventParameter = MenuProductUuidEventParameter(value = "uuid"),
                ),
                IncreaseCartProductClickEvent(
                    menuProductUuidEventParameter = MenuProductUuidEventParameter(value = "uuid"),
                ),
                DecreaseCartProductClickEvent(
                    menuProductUuidEventParameter = MenuProductUuidEventParameter(value = "uuid"),
                ),
                RemoveCartProductClickEvent(
                    menuProductUuidEventParameter = MenuProductUuidEventParameter(value = "uuid"),
                ),
                AddCartProductDetailsClickEvent(
                    menuProductUuidEventParameter = MenuProductUuidEventParameter(value = "uuid"),
                ),
                AddRecommendationProductClickEvent(
                    menuProductUuidEventParameter = MenuProductUuidEventParameter(value = "uuid"),
                ),
                AddRecommendationProductDetailsClickEvent(
                    menuProductUuidEventParameter = MenuProductUuidEventParameter(value = "uuid"),
                ),
                AddFavoriteClickEvent(
                    menuProductUuidEventParameter = MenuProductUuidEventParameter(value = "uuid"),
                ),
                RemoveFavoriteClickEvent(
                    menuProductUuidEventParameter = MenuProductUuidEventParameter(value = "uuid"),
                ),
                AuthConfirmErrorEvent(error = "InvalidCode"),
                LogoutSettingsClickEvent(),
                CartProceedClickEvent(isAuthorized = true),
                LoginRequestEvent(),
                LoginSuccessEvent(direction = "checkout"),
                CitySelectEvent(cityUuid = "city-uuid"),
                OrderCreateClickEvent(
                    isDelivery = true,
                    paymentMethod = "card",
                ),
                OrderCreateSuccessEvent(),
                OrderCreateFailEvent(error = "unknown"),
                OrderDeliveryToggleEvent(isDelivery = false),
                NotificationOpenEvent(campaign = "client_return", touch = "1", variant = "PERSONAL"),
            )

        events.forEach { event ->
            val eventName = event.eventName(prefix = FoodDeliveryEvent.LONGEST_PREFIX)
            assertTrue(
                actual = eventName.length <= FoodDeliveryEvent.MAX_EVENT_NAME_LENGTH,
                message = "Event name '$eventName' exceeds ${FoodDeliveryEvent.MAX_EVENT_NAME_LENGTH} chars",
            )
        }
    }
}
