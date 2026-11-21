package com.bunbeauty.profile.ui.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bunbeauty.designsystem.model.CityUI
import com.bunbeauty.designsystem.theme.FoodDeliveryTheme
import com.bunbeauty.designsystem.ui.LocalBottomBarPadding
import com.bunbeauty.designsystem.ui.element.FoodDeliveryScaffold
import com.bunbeauty.designsystem.ui.element.button.MainButton
import com.bunbeauty.designsystem.ui.element.button.TextButton
import com.bunbeauty.designsystem.ui.element.card.NavigationCardWithDivider
import com.bunbeauty.designsystem.ui.element.card.TextCardWithDivider
import com.bunbeauty.designsystem.ui.element.footer.WebStickyFooterColumn
import com.bunbeauty.designsystem.ui.screen.ErrorScreen
import com.bunbeauty.designsystem.ui.screen.LoadingScreen
import com.bunbeauty.profile.presentation.settings.SettingsState
import com.bunbeauty.profile.presentation.settings.SettingsViewModel
import com.bunbeauty.profile.ui.screen.city.screen.changecity.CityListBottomSheetScreen
import com.bunbeauty.profile.ui.screen.logout.LogoutBottomSheetScreen
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import papakarlo.designsystem.generated.resources.Res
import papakarlo.designsystem.generated.resources.action_logout
import papakarlo.designsystem.generated.resources.action_settings_delete
import papakarlo.designsystem.generated.resources.common_city
import papakarlo.designsystem.generated.resources.error_common_data_loading
import papakarlo.designsystem.generated.resources.error_something_went_wrong
import papakarlo.designsystem.generated.resources.hint_settings_phone
import papakarlo.designsystem.generated.resources.msg_settings_email_updated
import papakarlo.designsystem.generated.resources.msg_settings_marketing_notifications
import papakarlo.designsystem.generated.resources.title_settings
import papakarlo.designsystem.generated.resources.title_settings_email
import papakarlo.designsystem.generated.resources.title_settings_marketing_notifications

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel = koinViewModel(),
    back: () -> Unit,
    showInfoMessage: (String, Int) -> Unit,
    showErrorMessage: (String) -> Unit,
) {
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    val viewState by viewModel.dataState.collectAsStateWithLifecycle()

    val onAction =
        remember {
            { action: SettingsState.Action ->
                viewModel.onAction(action)
            }
        }

    val effects by viewModel.events.collectAsStateWithLifecycle()
    val consumeEffects =
        remember {
            {
                viewModel.consumeEvents(effects)
            }
        }

    SettingsEffect(
        effects = effects,
        back = back,
        consumeEffects = consumeEffects,
        showInfoMessage = showInfoMessage,
        showErrorMessage = showErrorMessage,
    )

    SettingsScreen(viewState = viewState.mapState(), onAction = onAction)
}

@Composable
fun SettingsState.DataState.mapState(): SettingsViewState =
    SettingsViewState(
        phoneNumber = settings?.phoneNumber.orEmpty(),
        selectedCityName = selectedCity?.name.orEmpty(),
        email = settings?.email.orEmpty(),
        emailInput = emailInput,
        isShowEmailBottomSheet = isShowEmailBottomSheet,
        marketingNotificationsEnabled = settings?.marketingNotificationsEnabled ?: true,
        state =
            when (state) {
                SettingsState.DataState.State.SUCCESS -> {
                    SettingsViewState.State.Success
                }

                SettingsState.DataState.State.ERROR -> {
                    SettingsViewState.State.Error
                }

                SettingsState.DataState.State.LOADING -> {
                    SettingsViewState.State.Loading
                }
            },
        logoutUI =
            SettingsViewState.LogoutBottomSheetUI(
                isShown = isShowLogoutBottomSheet,
            ),
        cityListBottomSheetUI =
            SettingsViewState.CityListBottomSheetUI(
                isShown = isShowCityListBottomSheet,
                cityListUI =
                    cityList
                        .map { city ->
                            CityUI(
                                uuid = city.uuid,
                                name = city.name,
                                isSelected = city.uuid == selectedCity?.uuid,
                            )
                        }.toPersistentList(),
            ),
        disableUserBottomSheetUI =
            SettingsViewState.DisableUserBottomSheetUI(
                isShown = isShowDisableUserBottomSheet,
            ),
    )

@Composable
fun SettingsEffect(
    effects: List<SettingsState.Event>,
    back: () -> Unit,
    consumeEffects: () -> Unit,
    showInfoMessage: (String, Int) -> Unit,
    showErrorMessage: (String) -> Unit,
) {
    LaunchedEffect(effects) {
        effects.forEach { effect ->
            when (effect) {
                SettingsState.Event.ShowEmailChangedSuccessfullyEvent -> {
                    showInfoMessage(
                        getString(Res.string.msg_settings_email_updated),
                        0,
                    )
                }

                SettingsState.Event.ShowEmailChangingFailedEvent -> {
                    showErrorMessage(getString(Res.string.error_something_went_wrong))
                }

                SettingsState.Event.ShowNotificationSettingsFailedEvent -> {
                    showErrorMessage(getString(Res.string.error_something_went_wrong))
                }

                SettingsState.Event.Back -> back()
            }
        }
        consumeEffects()
    }
}

@Composable
fun SettingsScreen(
    viewState: SettingsViewState,
    onAction: (SettingsState.Action) -> Unit,
) {
    FoodDeliveryScaffold(
        title = stringResource(Res.string.title_settings),
        backActionClick = {
            onAction(SettingsState.Action.BackClick)
        },
        actionButton = {
            MainButton(
                modifier =
                    Modifier
                        .padding(horizontal = FoodDeliveryTheme.dimensions.mediumSpace)
                        .padding(bottom = LocalBottomBarPadding.current),
                text = stringResource(resource = Res.string.action_logout),
                onClick = {
                    onAction(SettingsState.Action.OnLogoutClicked)
                },
            )
        },
        backgroundColor = FoodDeliveryTheme.colors.mainColors.surface,
    ) {
        when (viewState.state) {
            SettingsViewState.State.Success -> {
                SettingsScreenSuccess(
                    settingsState = viewState,
                    onAction = onAction,
                )
                LogoutBottomSheetScreen(
                    logoutUI = viewState.logoutUI,
                    onAction = onAction,
                )
                CityListBottomSheetScreen(
                    cityListBottomSheetUI = viewState.cityListBottomSheetUI,
                    onAction = onAction,
                )
                DisableUserBottomSheetScreen(
                    disableUserBottomSheetUI = viewState.disableUserBottomSheetUI,
                    onAction = onAction,
                )
                EmailBottomSheetScreen(
                    isShown = viewState.isShowEmailBottomSheet,
                    emailInput = viewState.emailInput,
                    onAction = onAction,
                )
            }

            SettingsViewState.State.Error ->
                ErrorScreen(
                    mainTextId = Res.string.error_common_data_loading,
                ) {
                    onAction(SettingsState.Action.LoadData)
                }

            SettingsViewState.State.Loading -> LoadingScreen()
        }
    }
}

@Composable
fun SettingsScreenSuccess(
    settingsState: SettingsViewState,
    onAction: (SettingsState.Action) -> Unit,
) {
    WebStickyFooterColumn(
        footerModifier =
            Modifier.padding(bottom = FoodDeliveryTheme.dimensions.scrollScreenBottomSpace),
    ) {
        TextCardWithDivider(
            label = stringResource(Res.string.hint_settings_phone),
            value = settingsState.phoneNumber,
        )

        NavigationCardWithDivider(
            onClick = {
                onAction(SettingsState.Action.OnEmailClicked)
            },
            label = stringResource(Res.string.title_settings_email),
            value = settingsState.email,
        )

        NavigationCardWithDivider(
            onClick = {
                onAction(SettingsState.Action.OnCityClicked)
            },
            label = stringResource(Res.string.common_city),
            value = settingsState.selectedCityName,
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(Res.string.title_settings_marketing_notifications),
                    style = FoodDeliveryTheme.typography.bodyLarge,
                    color = FoodDeliveryTheme.colors.mainColors.onSurface,
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = stringResource(Res.string.msg_settings_marketing_notifications),
                    style = FoodDeliveryTheme.typography.bodySmall,
                    color = FoodDeliveryTheme.colors.mainColors.onSurfaceVariant,
                )
            }
            Switch(
                modifier = Modifier.padding(start = 8.dp),
                checked = settingsState.marketingNotificationsEnabled,
                onCheckedChange = { enabled ->
                    onAction(SettingsState.Action.OnMarketingNotificationsChanged(enabled))
                },
                colors =
                    SwitchDefaults.colors(
                        checkedThumbColor = FoodDeliveryTheme.colors.mainColors.surface,
                        checkedTrackColor = FoodDeliveryTheme.colors.mainColors.primary,
                        uncheckedThumbColor = FoodDeliveryTheme.colors.mainColors.surface,
                        uncheckedTrackColor = FoodDeliveryTheme.colors.mainColors.onSurfaceVariant,
                        uncheckedBorderColor = FoodDeliveryTheme.colors.mainColors.onSurfaceVariant,
                    ),
            )
        }

        TextButton(
            modifier = Modifier.padding(top = 16.dp).padding(horizontal = 16.dp),
            textStringId = Res.string.action_settings_delete,
            onClick = {
                onAction(SettingsState.Action.DisableUser)
            },
            elevated = false,
        )
    }
}

val previewSettingsViewState =
    SettingsViewState(
        phoneNumber = "+7 999 000-00-00",
        selectedCityName = "Москва",
        state = SettingsViewState.State.Success,
        logoutUI = SettingsViewState.LogoutBottomSheetUI(false),
        cityListBottomSheetUI =
            SettingsViewState.CityListBottomSheetUI(
                isShown = false,
                cityListUI = persistentListOf(),
            ),
        disableUserBottomSheetUI =
            SettingsViewState.DisableUserBottomSheetUI(
                isShown = false,
            ),
    )

@Preview(showBackground = true)
@Composable
private fun SettingsScreenWithEmailPreview() {
    FoodDeliveryTheme {
        SettingsScreen(
            previewSettingsViewState.copy(
                phoneNumber = "+7 999 000-00-00",
                selectedCityName = "Москва",
                state = SettingsViewState.State.Success,
                logoutUI = SettingsViewState.LogoutBottomSheetUI(false),
                cityListBottomSheetUI =
                    SettingsViewState.CityListBottomSheetUI(
                        isShown = false,
                        cityListUI = persistentListOf(),
                    ),
            ),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenWithoutEmailPreview() {
    FoodDeliveryTheme {
        SettingsScreen(
            previewSettingsViewState.copy(
                phoneNumber = "+7 999 000-00-00",
                selectedCityName = "Москва",
                state = SettingsViewState.State.Success,
                logoutUI = SettingsViewState.LogoutBottomSheetUI(isShown = false),
                cityListBottomSheetUI =
                    SettingsViewState.CityListBottomSheetUI(
                        isShown = false,
                        cityListUI = persistentListOf(),
                    ),
            ),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenLoadingPreview() {
    FoodDeliveryTheme {
        SettingsScreen(
            previewSettingsViewState.copy(
                phoneNumber = "+7 999 000-00-00",
                selectedCityName = "Москва",
                state = SettingsViewState.State.Loading,
                logoutUI = SettingsViewState.LogoutBottomSheetUI(isShown = false),
                cityListBottomSheetUI =
                    SettingsViewState.CityListBottomSheetUI(
                        isShown = false,
                        cityListUI = persistentListOf(),
                    ),
            ),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenErrorPreview() {
    FoodDeliveryTheme {
        SettingsScreen(
            previewSettingsViewState.copy(
                phoneNumber = "+7 999 000-00-00",
                selectedCityName = "Москва",
                state = SettingsViewState.State.Error,
                logoutUI = SettingsViewState.LogoutBottomSheetUI(isShown = false),
                cityListBottomSheetUI =
                    SettingsViewState.CityListBottomSheetUI(
                        isShown = false,
                        cityListUI = persistentListOf(),
                    ),
            ),
            onAction = {},
        )
    }
}
