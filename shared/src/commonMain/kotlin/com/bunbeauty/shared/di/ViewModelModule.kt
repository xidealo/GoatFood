package com.bunbeauty.shared.di

import com.bunbeauty.order.presentation.consumercart.ConsumerCartViewModel
import com.bunbeauty.profile.presentation.selectcity.SelectCityViewModel
import com.bunbeauty.shared.presentation.MainViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun viewModelModule() =
    module {
        viewModel {
            MainViewModel(
                isOrderAvailableUseCase = get(),
                networkUtil = get(),
                userRepository = get(),
                analyticService = get(),
            )
        }
        viewModel {
            ConsumerCartViewModel(
                userInteractor = get(),
                cartProductInteractor = get(),
                increaseCartProductCountUseCase = get(),
                addMenuProductUseCase = get(),
                removeCartProductUseCase = get(),
                getRecommendationsUseCase = get(),
                getMotivationUseCase = get(),
                analyticService = get(),
                isOrderAvailableUseCase = get(),
            )
        }

        viewModel {
            SelectCityViewModel(
                cityInteractor = get(),
                checkOneCityUseCase = get(),
                analyticService = get(),
            )
        }
    }
