package com.bunbeauty.shared.di

import com.bunbeauty.core.domain.cafe.CafeInteractor
import com.bunbeauty.core.domain.cafe.ICafeInteractor
import com.bunbeauty.core.domain.cart.CartProductInteractor
import com.bunbeauty.core.domain.cart.ICartProductInteractor
import com.bunbeauty.core.domain.city.CityInteractor
import com.bunbeauty.core.domain.city.ICityInteractor
import com.bunbeauty.core.domain.menu_product.IMenuProductInteractor
import com.bunbeauty.core.domain.menu_product.MenuProductInteractor
import com.bunbeauty.core.domain.user.IUserInteractor
import com.bunbeauty.core.domain.user.UserInteractor
import org.koin.dsl.module

internal fun interactorModule() =
    module {
        single<IUserInteractor> {
            UserInteractor(
                userRepo = get(),
                orderRepo = get(),
                cafeRepo = get(),
                userAddressRepo = get(),
                createOrderSettingsRepo = get(),
                favoriteRepo = get(),
            )
        }
        single<ICityInteractor> {
            CityInteractor(
                cityRepo = get(),
            )
        }
        single {
            CityInteractor(
                cityRepo = get(),
            )
        }
        single<ICartProductInteractor> {
            CartProductInteractor(
                cartProductRepo = get(),
                getCartTotalFlowUseCase = get(),
                cartProductAdditionRepository = get(),
            )
        }
        single<ICafeInteractor> {
            CafeInteractor(
                cafeRepo = get(),
                menuProductRepo = get(),
            )
        }
        single<IMenuProductInteractor> {
            MenuProductInteractor(
                menuProductRepo = get(),
                getMenuProductListUseCase = get(),
            )
        }
    }
