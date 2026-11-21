package com.bunbeauty.shared.di.usecase

import com.bunbeauty.core.buildVersionQualifier
import com.bunbeauty.core.domain.DisableUserUseCase
import com.bunbeauty.core.domain.GetNewTotalCostUseCase
import com.bunbeauty.core.domain.GetNewTotalCostUseCaseImpl
import com.bunbeauty.core.domain.address.CreateAddressUseCase
import com.bunbeauty.core.domain.address.GetFilteredStreetListUseCase
import com.bunbeauty.core.domain.auth.ObserveTokenUseCase
import com.bunbeauty.core.domain.cart.GetCartTotalFlowUseCase
import com.bunbeauty.core.domain.cart.GetOldTotalCostUseCase
import com.bunbeauty.core.domain.cart.GetOldTotalCostUseCaseImpl
import com.bunbeauty.core.domain.city.GetSelectedCityTimeZoneUseCase
import com.bunbeauty.core.domain.city.GetSelectedCityTimeZoneUseCaseImpl
import com.bunbeauty.core.domain.deferred_time.GetMinTimeUseCase
import com.bunbeauty.core.domain.discount.GetDiscountUseCase
import com.bunbeauty.core.domain.discount.GetDiscountUseCaseImpl
import com.bunbeauty.core.domain.link.GetLinkUseCase
import com.bunbeauty.core.domain.notification.NotificationSettingsHandler
import com.bunbeauty.core.domain.menu_product.GetMenuProductListUseCase
import com.bunbeauty.core.domain.menu_product.GetMenuProductListUseCaseImpl
import com.bunbeauty.core.domain.menu_product.GetMenuProductUseCase
import com.bunbeauty.core.domain.settings.ObserveSettingsUseCase
import com.bunbeauty.core.domain.settings.RefreshSettingsUseCase
import com.bunbeauty.core.domain.settings.UpdateEmailUseCase
import com.bunbeauty.core.domain.splash.CheckOneCityUseCase
import com.bunbeauty.core.domain.splash.CheckUpdateUseCase
import com.bunbeauty.core.domain.splash.SaveOneCityUseCase
import com.bunbeauty.shared.domain.feature.notification.NotificationNavigation
import com.bunbeauty.shared.domain.feature.notification.NotificationSettingsHandlerImpl
import com.bunbeauty.shared.domain.feature.notification.SubscribeToNotificationUseCase
import com.bunbeauty.shared.domain.feature.notification.SyncNotificationSubscriptionUseCase
import com.bunbeauty.shared.domain.feature.notification.UpdateNotificationPermissionUseCase
import org.koin.dsl.module

internal fun useCaseModules() =
    module {
        factory {
            GetCartTotalFlowUseCase(
                cartProductRepo = get(),
                getDiscountUseCase = get(),
                getNewTotalCostUseCase = get(),
                getOldTotalCostUseCase = get(),
                getDeliveryCostFlowUseCase = get(),
            )
        }
        factory<GetNewTotalCostUseCase> {
            GetNewTotalCostUseCaseImpl(
                getDiscountUseCase = get(),
                getCartProductAdditionsPriceUseCase = get(),
            )
        }
        factory<GetOldTotalCostUseCase> {
            GetOldTotalCostUseCaseImpl(
                getCartProductAdditionsPriceUseCase = get(),
            )
        }

        factory {
            GetMinTimeUseCase(dateTimeUtil = get())
        }
        factory {
            DisableUserUseCase(
                userRepo = get(),
            )
        }
        factory {
            ObserveSettingsUseCase(
                settingsRepository = get(),
            )
        }
        factory {
            RefreshSettingsUseCase(
                settingsRepo = get(),
            )
        }
        factory {
            UpdateEmailUseCase(
                settingsRepository = get(),
            )
        }
        factory<GetSelectedCityTimeZoneUseCase> {
            GetSelectedCityTimeZoneUseCaseImpl(
                cityRepo = get(),
            )
        }
        factory {
            CreateAddressUseCase(
                userAddressRepo = get(),
                userRepo = get(),
            )
        }
        factory {
            GetMenuProductUseCase(
                menuProductRepo = get(),
            )
        }
        factory<GetMenuProductListUseCase> {
            GetMenuProductListUseCaseImpl(
                menuProductRepo = get(),
            )
        }
        factory {
            GetFilteredStreetListUseCase()
        }
        factory {
            GetLinkUseCase(
                linkRepo = get(),
            )
        }
        factory {
            SubscribeToNotificationUseCase()
        }
        single { NotificationNavigation() }
        single<NotificationSettingsHandler> {
            NotificationSettingsHandlerImpl(
                notificationPermissionRequester = get(),
                subscribeToNotificationUseCase = get(),
                companyUuidProvider = get(),
            )
        }
        factory {
            UpdateNotificationPermissionUseCase(userRepository = get())
        }
        factory {
            SyncNotificationSubscriptionUseCase(
                settingsRepository = get(),
                subscribeToNotificationUseCase = get(),
                companyUuidProvider = get(),
            )
        }
        factory<GetDiscountUseCase> {
            GetDiscountUseCaseImpl(
                discountRepository = get(),
                orderRepository = get(),
                settingsRepo = get(),
            )
        }
        factory {
            ObserveTokenUseCase(
                userRepo = get(),
            )
        }
        factory {
            CheckUpdateUseCase(
                versionRepo = get(),
                buildVersion = get(buildVersionQualifier),
            )
        }
        factory {
            CheckOneCityUseCase(cityRepo = get())
        }
        factory {
            SaveOneCityUseCase(
                cityRepo = get(),
            )
        }
    }
