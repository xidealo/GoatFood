package com.bunbeauty.profile.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bunbeauty.core.model.SuccessLoginDirection
import com.bunbeauty.designsystem.theme.FoodDeliveryTheme
import com.bunbeauty.designsystem.theme.bold
import com.bunbeauty.designsystem.ui.LocalBottomBarPadding
import com.bunbeauty.designsystem.ui.element.FoodDeliveryScaffold
import com.bunbeauty.designsystem.ui.element.button.MainButton
import com.bunbeauty.designsystem.ui.element.card.NavigationIconCardWithDivider
import com.bunbeauty.designsystem.ui.element.footer.WebSiteFooter
import com.bunbeauty.designsystem.ui.element.footer.WebStickyFooterColumn
import com.bunbeauty.designsystem.ui.screen.ErrorScreen
import com.bunbeauty.designsystem.ui.screen.LoadingScreen
import com.bunbeauty.profile.presentation.profile.ProfileState
import com.bunbeauty.profile.presentation.profile.ProfileViewModel
import com.bunbeauty.profile.ui.screen.aboutapp.AboutAppBottomSheet
import com.bunbeauty.profile.ui.screen.feedback.FeedBackBottomSheetScreen
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import papakarlo.designsystem.generated.resources.Res
import papakarlo.designsystem.generated.resources.action_profile_login
import papakarlo.designsystem.generated.resources.action_profile_my_addresses
import papakarlo.designsystem.generated.resources.action_profile_my_orders
import papakarlo.designsystem.generated.resources.action_profile_settings
import papakarlo.designsystem.generated.resources.description_empty_profile
import papakarlo.designsystem.generated.resources.description_ic_about
import papakarlo.designsystem.generated.resources.description_ic_feedback
import papakarlo.designsystem.generated.resources.description_ic_my_addresses
import papakarlo.designsystem.generated.resources.description_ic_my_orders
import papakarlo.designsystem.generated.resources.description_ic_settings
import papakarlo.designsystem.generated.resources.error_cafe_list_loading
import papakarlo.designsystem.generated.resources.ic_address
import papakarlo.designsystem.generated.resources.ic_cafes
import papakarlo.designsystem.generated.resources.ic_history
import papakarlo.designsystem.generated.resources.ic_info
import papakarlo.designsystem.generated.resources.ic_profile
import papakarlo.designsystem.generated.resources.ic_settings
import papakarlo.designsystem.generated.resources.ic_star
import papakarlo.designsystem.generated.resources.msg_profile_no_profile
import papakarlo.designsystem.generated.resources.title_about_app
import papakarlo.designsystem.generated.resources.title_feedback
import papakarlo.designsystem.generated.resources.title_profile
import papakarlo.designsystem.generated.resources.title_profile_cafe_list
import papakarlo.designsystem.generated.resources.title_profile_no_profile

@Composable
fun ProfileState.DataState.mapState(): ProfileViewState =
    ProfileViewState(
        state =
            when (state) {
                ProfileState.DataState.State.AUTHORIZED -> ProfileViewState.State.Authorized
                ProfileState.DataState.State.UNAUTHORIZED -> ProfileViewState.State.Unauthorized
                ProfileState.DataState.State.ERROR -> ProfileViewState.State.Error
                ProfileState.DataState.State.LOADING -> ProfileViewState.State.Loading
            },
        aboutBottomSheetUI =
            ProfileViewState.AboutBottomSheetUI(
                isShown = isShowAboutAppBottomSheet,
                version = appVersion,
            ),
        feedBackBottomSheetUI =
            ProfileViewState.FeedBackBottomSheetUI(
                isShown = isShowFeedbackBottomSheet,
                linkList =
                    linkList
                        .map { link ->
                            link.toUI()
                        }.toPersistentList(),
            ),
    )

@Composable
fun ProfileRoute(
    viewModel: ProfileViewModel = koinViewModel(),
    back: () -> Unit,
    goToUserAddress: (Boolean) -> Unit,
    goToLogin: (SuccessLoginDirection) -> Unit,
    goToOrdersFragment: () -> Unit,
    goToSettingsFragment: () -> Unit,
    goToCafeListFragment: () -> Unit,
) {
    val viewState by viewModel.dataState.collectAsStateWithLifecycle()

    val onAction =
        remember {
            { event: ProfileState.Action ->
                viewModel.onAction(event)
            }
        }

    val effects by viewModel.events.collectAsStateWithLifecycle()
    val consumeEffects =
        remember {
            {
                viewModel.consumeEvents(effects)
            }
        }

    ProfileEffect(
        effects = effects,
        back = back,
        goToUserAddress = goToUserAddress,
        goToLogin = goToLogin,
        goToOrdersFragment = goToOrdersFragment,
        goToSettingsFragment = goToSettingsFragment,
        goToCafeListFragment = goToCafeListFragment,
        consumeEffects = consumeEffects,
    )
    ProfileScreen(
        viewState = viewState.mapState(),
        onAction = onAction,
    )
}

@Composable
private fun ProfileScreen(
    viewState: ProfileViewState,
    onAction: (ProfileState.Action) -> Unit,
) {
    FoodDeliveryScaffold(
        title = stringResource(Res.string.title_profile),
        backActionClick = {
            onAction(ProfileState.Action.BackClicked)
        },
        backgroundColor = FoodDeliveryTheme.colors.mainColors.surface,
        actionButton = {
            if (viewState.state == ProfileViewState.State.Unauthorized) {
                LoginButton(onAction)
            }
        },
    ) {
        when (viewState.state) {
            ProfileViewState.State.Error ->
                ErrorScreen(
                    mainTextId = Res.string.error_cafe_list_loading,
                    onClick = {
                        onAction(ProfileState.Action.OnRefreshClicked)
                    },
                )

            ProfileViewState.State.Loading -> {
                LoadingScreen()
            }

            ProfileViewState.State.Authorized ->
                AuthorizedProfileScreen(
                    state = viewState,
                    onAction = onAction,
                )

            ProfileViewState.State.Unauthorized ->
                UnauthorizedProfileScreen(
                    onAction = onAction,
                    state = viewState,
                )
        }
        AboutAppBottomSheet(
            aboutBottomSheetUI = viewState.aboutBottomSheetUI,
            onAction = onAction,
            appVersion = viewState.aboutBottomSheetUI.version,
        )
        FeedBackBottomSheetScreen(
            feedBackBottomSheetUI = viewState.feedBackBottomSheetUI,
            onAction = onAction,
        )
    }
}

@Composable
fun ProfileEffect(
    effects: List<ProfileState.Event>,
    back: () -> Unit,
    goToUserAddress: (Boolean) -> Unit,
    goToLogin: (SuccessLoginDirection) -> Unit,
    goToOrdersFragment: () -> Unit,
    goToSettingsFragment: () -> Unit,
    goToCafeListFragment: () -> Unit,
    consumeEffects: () -> Unit,
) {
    LaunchedEffect(effects) {
        effects.forEach { effect ->
            when (effect) {
                ProfileState.Event.OpenAddressList -> {
                    goToUserAddress(false)
                }

                ProfileState.Event.OpenLogin -> {
                    goToLogin(SuccessLoginDirection.BACK_TO_PROFILE)
                }

                ProfileState.Event.OpenOrderList -> {
                    goToOrdersFragment()
                }

                ProfileState.Event.OpenSettings -> {
                    goToSettingsFragment()
                }

                ProfileState.Event.ShowCafeList -> {
                    goToCafeListFragment()
                }

                ProfileState.Event.GoBackEvent -> back()
            }
        }
        consumeEffects()
    }
}

@Composable
private fun LoginButton(onAction: (ProfileState.Action) -> Unit) {
    MainButton(
        modifier =
            Modifier
                .padding(horizontal = FoodDeliveryTheme.dimensions.mediumSpace)
                .padding(bottom = LocalBottomBarPadding.current),
        textStringId = Res.string.action_profile_login,
        onClick = { onAction(ProfileState.Action.OnLoginClicked) },
    )
}

@Composable
private fun AuthorizedProfileScreen(
    state: ProfileViewState,
    onAction: (ProfileState.Action) -> Unit,
) {
    WebStickyFooterColumn {
        NavigationIconCardWithDivider(
            modifier = Modifier.fillMaxWidth(),
            iconId = Res.drawable.ic_settings,
            iconDescriptionStringId = Res.string.description_ic_settings,
            labelStringId = Res.string.action_profile_settings,
            onClick = { onAction(ProfileState.Action.OnSettingsClick) },
        )
        NavigationIconCardWithDivider(
            modifier =
                Modifier
                    .fillMaxWidth(),
            iconId = Res.drawable.ic_address,
            iconDescriptionStringId = Res.string.description_ic_my_addresses,
            labelStringId = Res.string.action_profile_my_addresses,
            onClick = { onAction(ProfileState.Action.OnYourAddressesClicked) },
        )
        NavigationIconCardWithDivider(
            modifier =
                Modifier
                    .fillMaxWidth(),
            iconId = Res.drawable.ic_history,
            iconDescriptionStringId = Res.string.description_ic_my_orders,
            labelStringId = Res.string.action_profile_my_orders,
            onClick = { onAction(ProfileState.Action.OnOrderHistoryClicked) },
        )
        ProfileInfoCards(
            modifier = Modifier,
            onAction = onAction,
            state = state,
        )
    }
}

@Composable
private fun UnauthorizedProfileScreen(
    onAction: (ProfileState.Action) -> Unit,
    state: ProfileViewState,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        ProfileInfoCards(
            modifier = Modifier,
            onAction = onAction,
            state = state,
        )
        Spacer(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier.padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(
                            FoodDeliveryTheme.colors.mainColors.primary.copy(
                                alpha = 0.6f,
                            ),
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    modifier = Modifier.size(64.dp),
                    painter = painterResource(Res.drawable.ic_profile),
                    tint = FoodDeliveryTheme.colors.statusColors.onStatus,
                    contentDescription = stringResource(Res.string.description_empty_profile),
                )
            }
            Text(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                text = stringResource(Res.string.title_profile_no_profile),
                style = FoodDeliveryTheme.typography.titleMedium.bold,
                color = FoodDeliveryTheme.colors.mainColors.onBackground,
                textAlign = TextAlign.Center,
            )
            Text(
                modifier =
                    Modifier
                        .padding(top = 8.dp)
                        .padding(horizontal = 16.dp),
                text = stringResource(Res.string.msg_profile_no_profile),
                style = FoodDeliveryTheme.typography.bodyLarge,
                color = FoodDeliveryTheme.colors.mainColors.onBackground,
                textAlign = TextAlign.Center,
            )
        }
        Spacer(modifier = Modifier.weight(1f))

        WebSiteFooter()

        Spacer(modifier = Modifier.height(FoodDeliveryTheme.dimensions.scrollScreenBottomSpace))
    }
}

@Composable
private fun ProfileInfoCards(
    state: ProfileViewState,
    modifier: Modifier = Modifier,
    onAction: (ProfileState.Action) -> Unit,
) {
    Column(modifier = modifier) {
        NavigationIconCardWithDivider(
            modifier = Modifier.fillMaxWidth(),
            iconId = Res.drawable.ic_cafes,
            iconDescriptionStringId = Res.string.title_profile_cafe_list,
            labelStringId = Res.string.title_profile_cafe_list,
            onClick = { onAction(ProfileState.Action.OnCafeListClicked) },
        )
        NavigationIconCardWithDivider(
            modifier =
                Modifier
                    .fillMaxWidth(),
            iconId = Res.drawable.ic_star,
            iconDescriptionStringId = Res.string.description_ic_feedback,
            labelStringId = Res.string.title_feedback,
            onClick = {
                onAction(
                    ProfileState.Action.OnFeedbackClicked,
                )
            },
        )
        NavigationIconCardWithDivider(
            modifier =
                Modifier
                    .fillMaxWidth(),
            iconId = Res.drawable.ic_info,
            iconDescriptionStringId = Res.string.description_ic_about,
            labelStringId = Res.string.title_about_app,
            onClick = { onAction(ProfileState.Action.OnAboutAppClicked) },
        )
    }
}

val profileViewStateMock =
    ProfileViewState(
        state = ProfileViewState.State.Loading,
        aboutBottomSheetUI =
            ProfileViewState.AboutBottomSheetUI(
                isShown = false,
                version = "",
            ),
        feedBackBottomSheetUI =
            ProfileViewState.FeedBackBottomSheetUI(
                isShown = false,
                linkList = persistentListOf(),
            ),
    )

@Preview(showBackground = true)
@Composable
private fun AuthorizedProfileScreenWithLastOrderPreview() {
    FoodDeliveryTheme {
        ProfileScreen(
            viewState =
                profileViewStateMock.copy(
                    state = ProfileViewState.State.Authorized,
                ),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthorizedProfileScreenWithoutLastOrderPreview() {
    FoodDeliveryTheme {
        ProfileScreen(
            viewState =
                profileViewStateMock.copy(
                    state = ProfileViewState.State.Authorized,
                ),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UnauthorizedProfileScreenPreview() {
    FoodDeliveryTheme {
        ProfileScreen(
            viewState =
                profileViewStateMock.copy(
                    state = ProfileViewState.State.Unauthorized,
                ),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingProfileScreenPreview() {
    FoodDeliveryTheme {
        ProfileScreen(
            viewState =
                profileViewStateMock.copy(
                    state = ProfileViewState.State.Loading,
                ),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorProfileScreenPreview() {
    FoodDeliveryTheme {
        ProfileScreen(
            viewState =
                profileViewStateMock.copy(
                    state = ProfileViewState.State.Error,
                ),
            onAction = {},
        )
    }
}
