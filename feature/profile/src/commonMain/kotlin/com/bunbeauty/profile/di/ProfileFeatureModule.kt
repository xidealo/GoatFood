package com.bunbeauty.profile.di

import com.bunbeauty.core.buildVersionQualifier
import com.bunbeauty.profile.presentation.profile.ProfileViewModel
import com.bunbeauty.profile.presentation.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun profileFeatureModule() =
    module {
        viewModel {
            ProfileViewModel(
                observeTokenUseCase = get(),
                getLinkListUseCase = get(),
                buildVersion = get(buildVersionQualifier),
            )
        }
        viewModel {
            SettingsViewModel(
                observeSettingsUseCase = get(),
                observeSelectedCityUseCase = get(),
                getCityListUseCase = get(),
                saveSelectedCityUseCase = get(),
                disableUserUseCase = get(),
                userInteractor = get(),
                analyticService = get(),
                settingsRepository = get(),
                notificationSettingsHandler = get(),
                updateEmailUseCase = get(),
            )
        }
    }
