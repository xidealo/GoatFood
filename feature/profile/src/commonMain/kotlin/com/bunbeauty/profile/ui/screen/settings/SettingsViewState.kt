package com.bunbeauty.profile.ui.screen.settings

import androidx.compose.runtime.Immutable
import com.bunbeauty.core.base.BaseViewState
import com.bunbeauty.designsystem.model.CityUI
import kotlinx.collections.immutable.ImmutableList

data class SettingsViewState(
    val phoneNumber: String,
    val selectedCityName: String,
    val email: String = "",
    val emailInput: String = "",
    val isShowEmailBottomSheet: Boolean = false,
    val marketingNotificationsEnabled: Boolean = true,
    val state: State,
    val logoutUI: LogoutBottomSheetUI,
    val cityListBottomSheetUI: CityListBottomSheetUI,
    val disableUserBottomSheetUI: DisableUserBottomSheetUI,
) : BaseViewState {
    @Immutable
    data class LogoutBottomSheetUI(
        val isShown: Boolean,
    )

    @Immutable
    data class DisableUserBottomSheetUI(
        val isShown: Boolean,
    )

    @Immutable
    data class CityListBottomSheetUI(
        val isShown: Boolean,
        val cityListUI: ImmutableList<CityUI>,
    )

    enum class State {
        Success,
        Loading,
        Error,
    }
}
