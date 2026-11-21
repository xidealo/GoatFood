package com.bunbeauty.shared.di

import com.bunbeauty.address.di.addressFeatureModule
import com.bunbeauty.analytic.AnalyticService
import com.bunbeauty.analytic.di.analyticModule
import com.bunbeauty.auth.di.authFeatureModule
import com.bunbeauty.auth.domain.UpdateNotificationUseCase
import com.bunbeauty.cafe.di.cafeFeatureModule
import com.bunbeauty.core.domain.DisableUserUseCase
import com.bunbeauty.core.domain.ObserveCartUseCase
import com.bunbeauty.core.domain.addition.GetAdditionGroupsWithSelectedAdditionUseCase
import com.bunbeauty.core.domain.addition.GetPriceOfSelectedAdditionsUseCase
import com.bunbeauty.core.domain.address.CreateAddressUseCase
import com.bunbeauty.core.domain.address.GetCurrentUserAddressUseCase
import com.bunbeauty.core.domain.address.GetFilteredStreetListUseCase
import com.bunbeauty.core.domain.address.GetSelectableUserAddressListUseCase
import com.bunbeauty.core.domain.address.GetSuggestionsUseCase
import com.bunbeauty.core.domain.address.GetUserAddressListUseCase
import com.bunbeauty.core.domain.address.SaveSelectedUserAddressUseCase
import com.bunbeauty.core.domain.address.di.addressModule
import com.bunbeauty.core.domain.auth.CheckCodeUseCase
import com.bunbeauty.core.domain.auth.CheckPhoneNumberUseCase
import com.bunbeauty.core.domain.auth.FormatPhoneNumberUseCase
import com.bunbeauty.core.domain.auth.GetPhoneNumberCursorPositionUseCase
import com.bunbeauty.core.domain.auth.RequestCodeUseCase
import com.bunbeauty.core.domain.auth.ResendCodeUseCase
import com.bunbeauty.core.domain.cafe.GetAdditionalUtensilsUseCase
import com.bunbeauty.core.domain.cafe.GetCafeListUseCase
import com.bunbeauty.core.domain.cafe.GetDeferredTimeHintUseCase
import com.bunbeauty.core.domain.cafe.GetSelectableCafeListUseCase
import com.bunbeauty.core.domain.cafe.GetWorkloadCafeUseCase
import com.bunbeauty.core.domain.cafe.HasOpenedCafeUseCase
import com.bunbeauty.core.domain.cafe.ICafeInteractor
import com.bunbeauty.core.domain.cafe.IsDeliveryEnabledFromCafeUseCase
import com.bunbeauty.core.domain.cafe.IsPickupEnabledFromCafeUseCase
import com.bunbeauty.core.domain.cafe.ObserveCafeWithOpenStateListUseCase
import com.bunbeauty.core.domain.cafe.di.cafeModule
import com.bunbeauty.core.domain.cart.AddCartProductUseCase
import com.bunbeauty.core.domain.cart.EditCartProductUseCase
import com.bunbeauty.core.domain.cart.GetCartTotalFlowUseCase
import com.bunbeauty.core.domain.cart.GetRecommendationsUseCase
import com.bunbeauty.core.domain.cart.ICartProductInteractor
import com.bunbeauty.core.domain.cart.IncreaseCartProductCountUseCase
import com.bunbeauty.core.domain.cart.RemoveCartProductUseCase
import com.bunbeauty.core.domain.cart.di.cartModule
import com.bunbeauty.core.domain.favorite.di.favoriteModule
import com.bunbeauty.core.domain.city.GetCityListUseCase
import com.bunbeauty.core.domain.city.GetSelectedCityTimeZoneUseCase
import com.bunbeauty.core.domain.city.ICityInteractor
import com.bunbeauty.core.domain.city.ObserveSelectedCityUseCase
import com.bunbeauty.core.domain.city.SaveSelectedCityUseCase
import com.bunbeauty.core.domain.deferred_time.GetMinTimeUseCase
import com.bunbeauty.core.domain.discount.GetDiscountUseCase
import com.bunbeauty.core.domain.link.GetLinkListUseCase
import com.bunbeauty.core.domain.link.GetLinkUseCase
import com.bunbeauty.core.domain.menu.di.menuModule
import com.bunbeauty.core.domain.menu_product.AddMenuProductUseCase
import com.bunbeauty.core.domain.menu_product.GetMenuProductUseCase
import com.bunbeauty.core.domain.menu_product.IMenuProductInteractor
import com.bunbeauty.core.domain.motivation.GetMotivationUseCase
import com.bunbeauty.core.domain.order.CreateOrderUseCase
import com.bunbeauty.core.domain.order.GetExtendedCommentUseCase
import com.bunbeauty.core.domain.order.GetLastOrderUseCase
import com.bunbeauty.core.domain.order.GetWithoutUtensilsUseCase
import com.bunbeauty.core.domain.order.ObserveLastOrderUseCase
import com.bunbeauty.core.domain.order.ObserveOrderListUseCase
import com.bunbeauty.core.domain.order.ObserveOrderUseCase
import com.bunbeauty.core.domain.order.SaveWithoutUtensilsUseCase
import com.bunbeauty.core.domain.order.StopObserveOrdersUseCase
import com.bunbeauty.core.domain.orderavailable.IsOrderAvailableUseCase
import com.bunbeauty.core.domain.payment.GetPaymentMethodListUseCase
import com.bunbeauty.core.domain.payment.GetSelectablePaymentMethodListUseCase
import com.bunbeauty.core.domain.payment.GetSelectedPaymentMethodUseCase
import com.bunbeauty.core.domain.payment.SavePaymentMethodUseCase
import com.bunbeauty.core.domain.settings.ObserveSettingsUseCase
import com.bunbeauty.core.domain.settings.UpdateEmailUseCase
import com.bunbeauty.core.domain.splash.CheckOneCityUseCase
import com.bunbeauty.core.domain.splash.CheckUpdateUseCase
import com.bunbeauty.core.domain.splash.SaveOneCityUseCase
import com.bunbeauty.core.domain.user.IUserInteractor
import com.bunbeauty.createorder.di.createOrderFeatureModule
import com.bunbeauty.menu.di.menuFeatureModule
import com.bunbeauty.order.di.orderFeatureModule
import com.bunbeauty.productdetails.di.productDetailsFeatureModule
import com.bunbeauty.profile.di.profileFeatureModule
import com.bunbeauty.shared.data.CompanyUuidProvider
import com.bunbeauty.shared.data.di.dataMapperModule
import com.bunbeauty.shared.data.di.databaseModule
import com.bunbeauty.shared.data.di.networkModule
import com.bunbeauty.shared.data.di.providerModule
import com.bunbeauty.shared.data.di.repositoryModule
import com.bunbeauty.shared.data.di.storageModule
import com.bunbeauty.shared.data.mapper.user_address.UserAddressMapper
import com.bunbeauty.shared.data.network.api.NetworkConnector
import com.bunbeauty.shared.di.usecase.additionUseCaseModule
import com.bunbeauty.shared.di.usecase.authUseCaseModule
import com.bunbeauty.shared.di.usecase.cityUseCaseModule
import com.bunbeauty.shared.di.usecase.orderUseCaseModule
import com.bunbeauty.shared.di.usecase.paymentUseCaseModule
import com.bunbeauty.shared.di.usecase.useCaseModules
import com.bunbeauty.shared.di.usecase.userAddressUseCaseModule
import com.bunbeauty.shared.domain.feature.notification.NotificationNavigation
import com.bunbeauty.shared.domain.feature.notification.SubscribeToNotificationUseCase
import com.bunbeauty.shared.domain.feature.notification.SyncNotificationSubscriptionUseCase
import com.bunbeauty.shared.domain.feature.notification.UpdateNotificationPermissionUseCase
import com.bunbeauty.splash.di.splashFeatureModule
import com.bunbeauty.update.di.updateFeatureModule
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            networkModule(),
            databaseModule(),
            providerModule(),
            dataMapperModule(),
            repositoryModule(),
            storageModule(),
            interactorModule(),
            utilModule(),
            platformModule(),
            domainMapperModule(),
            cityUseCaseModule(),
            userAddressUseCaseModule(),
            orderUseCaseModule(),
            cartModule(),
            favoriteModule(),
            cafeModule(),
            paymentUseCaseModule(),
            authUseCaseModule(),
            useCaseModules(),
            analyticModule(),
            additionUseCaseModule(),
            menuModule(),
            menuFeatureModule(),
            addressModule(),
            viewModelModule(),
            profileFeatureModule(),
            productDetailsFeatureModule(),
            authFeatureModule(),
            addressFeatureModule(),
            splashFeatureModule(),
            updateFeatureModule(),
            cafeFeatureModule(),
            orderFeatureModule(),
            createOrderFeatureModule(),
        )
    }

/**
 * Function for init in IOS
 * */
fun initKoin() =
    startKoin {
        modules(
            databaseModule(),
            platformModule(),
            viewModelModule(),
            providerModule(),
            networkModule(),
            dataMapperModule(),
            repositoryModule(),
            storageModule(),
            interactorModule(),
            utilModule(),
            domainMapperModule(),
            cityUseCaseModule(),
            userAddressUseCaseModule(),
            orderUseCaseModule(),
            cartModule(),
            favoriteModule(),
            cafeModule(),
            paymentUseCaseModule(),
            authUseCaseModule(),
            useCaseModules(),
            analyticModule(),
            additionUseCaseModule(),
            menuModule(),
            menuFeatureModule(),
            addressModule(),
            profileFeatureModule(),
            productDetailsFeatureModule(),
            authFeatureModule(),
            addressFeatureModule(),
            splashFeatureModule(),
            updateFeatureModule(),
            cafeFeatureModule(),
            orderFeatureModule(),
            createOrderFeatureModule(),
        )
    }

class IosComponent : KoinComponent {
    // Interactors
    fun provideCityInteractor(): ICityInteractor = get()

    fun provideMenuInteractor(): IMenuProductInteractor = get()

    fun provideCafeInteractor(): ICafeInteractor = get()

    fun provideCartProductInteractor(): ICartProductInteractor = get()

    fun provideIUserInteractor(): IUserInteractor = get()

    // Use cases
    fun provideDisableUserUseCase(): DisableUserUseCase = get()

    fun provideGetUserAddressListUseCase(): GetUserAddressListUseCase = get()

    fun provideGetSelectableUserAddressListUseCase(): GetSelectableUserAddressListUseCase = get()

    fun provideGetCafeListUseCase(): GetCafeListUseCase = get()

    fun provideGetSelectableCafeListUseCase(): GetSelectableCafeListUseCase = get()

    fun provideGetCartTotalUseCase(): GetCartTotalFlowUseCase = get()

    fun provideGetMinTimeUseCase(): GetMinTimeUseCase = get()

    fun provideCreateOrderUseCase(): CreateOrderUseCase = get()

    fun provideGetSelectedCityTimeZoneUseCase(): GetSelectedCityTimeZoneUseCase = get()

    fun provideObserveSettingsUseCase(): ObserveSettingsUseCase = get()

    fun provideObserveSelectedCityUseCase(): ObserveSelectedCityUseCase = get()

    fun provideUpdateEmailUseCase(): UpdateEmailUseCase = get()

    fun provideGetCityListUseCase(): GetCityListUseCase = get()

    fun provideSaveSelectedCityUseCase(): SaveSelectedCityUseCase = get()

    fun provideObserveLastOrderUseCase(): ObserveLastOrderUseCase = get()

    fun provideStopObserveOrdersUseCase(): StopObserveOrdersUseCase = get()

    fun provideObserveOrderListUseCase(): ObserveOrderListUseCase = get()

    fun provideObserveOrderUseCase(): ObserveOrderUseCase = get()

    fun provideCreateAddressUseCase(): CreateAddressUseCase = get()

    fun provideSaveSelectedUserAddressUseCase(): SaveSelectedUserAddressUseCase = get()

    fun provideGetLastOrderUseCase(): GetLastOrderUseCase = get()

    fun provideGetFilteredStreetListUseCase(): GetFilteredStreetListUseCase = get()

    fun provideObserveCartUseCase(): ObserveCartUseCase = get()

    fun provideRemoveCartProductUseCase(): RemoveCartProductUseCase = get()

    fun provideGetLinkListUseCase(): GetLinkListUseCase = get()

    fun provideGetLinkUseCase(): GetLinkUseCase = get()

    fun provideGetPaymentMethodListUseCase(): GetPaymentMethodListUseCase = get()

    fun provideSubscribeToNotificationUseCase(): SubscribeToNotificationUseCase = get()

    fun provideUpdateNotificationPermissionUseCase(): UpdateNotificationPermissionUseCase = get()

    fun provideNotificationNavigation(): NotificationNavigation = get()

    fun provideSyncNotificationSubscriptionUseCase(): SyncNotificationSubscriptionUseCase = get()

    fun provideGetSelectablePaymentMethodListUseCase(): GetSelectablePaymentMethodListUseCase = get()

    fun provideAddCartProductUseCase(): AddCartProductUseCase = get()

    fun provideGetDiscountUseCase(): GetDiscountUseCase = get()

    fun provideSavePaymentMethodUseCase(): SavePaymentMethodUseCase = get()

    fun provideRequestCodeUseCase(): RequestCodeUseCase = get()

    fun provideFormatPhoneNumberUseCase(): FormatPhoneNumberUseCase = get()

    fun provideGetPhoneNumberCursorPositionUseCase(): GetPhoneNumberCursorPositionUseCase = get()

    fun provideCheckCodeUseCase(): CheckCodeUseCase = get()

    fun provideResendCodeUseCase(): ResendCodeUseCase = get()

    fun provideCheckPhoneNumberUseCase(): CheckPhoneNumberUseCase = get()

    fun provideGetRecommendationsUseCase(): GetRecommendationsUseCase = get()

    fun provideGetMenuProductByUuidUseCase(): GetMenuProductUseCase = get()

    fun provideGetSuggestionsUseCase(): GetSuggestionsUseCase = get()

    fun provideIncreaseCartProductCountUseCase(): IncreaseCartProductCountUseCase = get()

    fun provideAddMenuProductUseCase(): AddMenuProductUseCase = get()

    fun provideEditCartProductUseCase(): EditCartProductUseCase = get()

    fun provideGetAdditionGroupsWithSelectedAdditionUseCase(): GetAdditionGroupsWithSelectedAdditionUseCase = get()

    fun provideGetPriceOfSelectedAdditionsUseCase(): GetPriceOfSelectedAdditionsUseCase = get()

    fun provideObserveCafeWithOpenStateListUseCase(): ObserveCafeWithOpenStateListUseCase = get()

    fun provideGetMotivationUseCaseUseCase(): GetMotivationUseCase = get()

    fun provideIsOrderAvailableUseCase(): IsOrderAvailableUseCase = get()

    fun provideCheckUpdateUseCase(): CheckUpdateUseCase = get()

    fun provideCheckOneCityUseCase(): CheckOneCityUseCase = get()

    fun provideSaveOneCityUseCase(): SaveOneCityUseCase = get()

    fun provideGetCurrentUserAddressUseCase(): GetCurrentUserAddressUseCase = get()

    fun provideIsPickupEnabledFromCafeUseCase(): IsPickupEnabledFromCafeUseCase = get()

    fun provideHasOpenedCafeUseCase(): HasOpenedCafeUseCase = get()

    fun provideGetWorkloadCafeUseCase(): GetWorkloadCafeUseCase = get()

    fun provideIsDeliveryEnabledFromCafeUseCase(): IsDeliveryEnabledFromCafeUseCase = get()

    fun provideUpdateNotificationUseCase(): UpdateNotificationUseCase = get()

    fun provideGetCurrentUserAddressWithCityUseCase(): GetCurrentUserAddressUseCase = get()

    fun provideGetExtendedCommentUseCase(): GetExtendedCommentUseCase = get()

    fun provideGetSelectedPaymentMethodUseCase(): GetSelectedPaymentMethodUseCase = get()

    fun provideGetAdditionalUtensilsUseCase(): GetAdditionalUtensilsUseCase = get()

    fun provideGetDeferredTimeHintUseCase(): GetDeferredTimeHintUseCase = get()

    fun provideGetWithoutUtensilsUseCase(): GetWithoutUtensilsUseCase = get()

    fun provideSaveWithoutUtensilsUseCase(): SaveWithoutUtensilsUseCase = get()

    // Mapper
    fun provideUserAddressMapper(): UserAddressMapper = get()

    // Other
    fun provideApiRepo(): NetworkConnector = get()

    fun provideAnalyticService(): AnalyticService = get()

    fun provideCompanyUuidProvider(): CompanyUuidProvider = get()
}
