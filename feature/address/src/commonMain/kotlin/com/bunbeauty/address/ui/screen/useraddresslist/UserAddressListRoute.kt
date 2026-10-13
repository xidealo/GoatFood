package com.bunbeauty.address.ui.screen.useraddresslist

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bunbeauty.address.presentation.user_address_list.UserAddressListDataState
import com.bunbeauty.address.presentation.user_address_list.UserAddressListViewModel
import com.bunbeauty.core.extension.toUserAddressItem
import com.bunbeauty.core.model.UserAddressItem
import com.bunbeauty.designsystem.theme.FoodDeliveryTheme
import com.bunbeauty.designsystem.ui.LocalBottomBarPadding
import com.bunbeauty.designsystem.ui.element.FoodDeliveryHorizontalDivider
import com.bunbeauty.designsystem.ui.element.FoodDeliveryScaffold
import com.bunbeauty.designsystem.ui.element.button.MainButton
import com.bunbeauty.designsystem.ui.element.footer.WebStickyFooterLazyColumn
import com.bunbeauty.designsystem.ui.element.selectable.SelectableItem
import com.bunbeauty.designsystem.ui.screen.EmptyScreen
import com.bunbeauty.designsystem.ui.screen.ErrorScreen
import com.bunbeauty.designsystem.ui.screen.LoadingScreen
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import papakarlo.designsystem.generated.resources.Res
import papakarlo.designsystem.generated.resources.action_add_addresses
import papakarlo.designsystem.generated.resources.description_cafe_addresses_empty
import papakarlo.designsystem.generated.resources.error_addresses_list_loading
import papakarlo.designsystem.generated.resources.ic_address
import papakarlo.designsystem.generated.resources.msg_my_addresses_empty
import papakarlo.designsystem.generated.resources.title_my_addresses
import papakarlo.designsystem.generated.resources.title_my_addresses_empty

@Composable
private fun UserAddressListDataState.DataState.mapState(): UserAddressListViewState =
    UserAddressListViewState(
        userAddressItems =
            userAddressList.map { userAddressList ->
                userAddressList.toUserAddressItem()
            },
        state =
            when (state) {
                UserAddressListDataState.DataState.State.SUCCESS -> UserAddressListViewState.State.Success
                UserAddressListDataState.DataState.State.ERROR -> UserAddressListViewState.State.Error
                UserAddressListDataState.DataState.State.EMPTY -> UserAddressListViewState.State.Empty
                UserAddressListDataState.DataState.State.LOADING -> UserAddressListViewState.State.Loading
            },
    )

@Composable
fun UserAddressListRoute(
    viewModel: UserAddressListViewModel = koinViewModel(),
    back: () -> Unit,
    goToCreateAddress: () -> Unit,
) {
    LaunchedEffect(Unit) {
        viewModel.onAction(UserAddressListDataState.Action.Init)
    }

    val viewState by viewModel.dataState.collectAsStateWithLifecycle()

    val effects by viewModel.events.collectAsStateWithLifecycle()
    val consumeEffects =
        remember {
            {
                viewModel.consumeEvents(effects)
            }
        }

    val onAction =
        remember {
            { event: UserAddressListDataState.Action ->
                viewModel.onAction(event)
            }
        }

    UserAddressEffect(
        effects = effects,
        back = back,
        goToCreateAddress = goToCreateAddress,
        consumeEffects = consumeEffects,
    )

    UserAddressListScreen(
        viewState = viewState.mapState(),
        onAction = onAction,
    )
}

@Composable
private fun UserAddressListScreen(
    viewState: UserAddressListViewState,
    onAction: (UserAddressListDataState.Action) -> Unit,
) {
    FoodDeliveryScaffold(
        title = stringResource(Res.string.title_my_addresses),
        backActionClick = { onAction(UserAddressListDataState.Action.BackClicked) },
        actionButton = {
            if (viewState.state != UserAddressListViewState.State.Loading) {
                MainButton(
                    modifier =
                        Modifier
                            .padding(horizontal = FoodDeliveryTheme.dimensions.mediumSpace)
                            .padding(bottom = LocalBottomBarPadding.current),
                    textStringId = Res.string.action_add_addresses,
                    onClick = { onAction(UserAddressListDataState.Action.OnClickedCreateAddress) },
                )
            }
        },
        backgroundColor = FoodDeliveryTheme.colors.mainColors.surface,
    ) {
        when (viewState.state) {
            UserAddressListViewState.State.Error ->
                ErrorScreen(
                    mainTextId = Res.string.error_addresses_list_loading,
                    onClick = {
                        onAction(UserAddressListDataState.Action.OnRefreshClicked)
                    },
                )

            UserAddressListViewState.State.Loading ->
                LoadingScreen()

            UserAddressListViewState.State.Success ->
                UserAddressListSuccessScreen(viewState.userAddressItems)

            UserAddressListViewState.State.Empty ->
                EmptyScreen(
                    imageId = Res.drawable.ic_address,
                    imageDescriptionId = Res.string.description_cafe_addresses_empty,
                    mainTextId = Res.string.title_my_addresses_empty,
                    extraTextId = Res.string.msg_my_addresses_empty,
                )
        }
    }
}

@Composable
private fun UserAddressListSuccessScreen(userAddressItems: List<UserAddressItem>) {
    WebStickyFooterLazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = spacedBy(8.dp),
        footerModifier =
            Modifier.padding(
                bottom =
                    FoodDeliveryTheme.dimensions.scrollScreenBottomSpace +
                        LocalBottomBarPadding.current,
            ),
    ) {
        items(userAddressItems) { userAddressItem ->
            Column {
                SelectableItem(
                    title = userAddressItem.address,
                    clickable = false,
                    elevated = false,
                    onClick = {},
                    enabled = true,
                )
                FoodDeliveryHorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }
    }
}

@Composable
fun UserAddressEffect(
    effects: List<UserAddressListDataState.Event>,
    back: () -> Unit,
    goToCreateAddress: () -> Unit,
    consumeEffects: () -> Unit,
) {
    LaunchedEffect(effects) {
        effects.forEach { effect ->
            when (effect) {
                UserAddressListDataState.Event.GoBackEvent -> back()
                UserAddressListDataState.Event.OpenCreateAddressEvent -> goToCreateAddress()
            }
        }
        consumeEffects()
    }
}

@Preview(showBackground = true)
@Composable
private fun UserAddressListSuccessScreenPreview() {
    FoodDeliveryTheme {
        val addressItemModel =
            UserAddressItem(
                uuid = "1",
                address = "addddd",
                isSelected = false,
            )
        UserAddressListScreen(
            UserAddressListViewState(
                userAddressItems =
                    listOf(
                        addressItemModel,
                        addressItemModel,
                        addressItemModel,
                        addressItemModel,
                    ),
                state = UserAddressListViewState.State.Success,
            ),
            onAction = { },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UserAddressListEmptyScreenPreview() {
    FoodDeliveryTheme {
        UserAddressListScreen(
            viewState =
                UserAddressListViewState(
                    state = UserAddressListViewState.State.Empty,
                ),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UserAddressListLoadingScreenPreview() {
    FoodDeliveryTheme {
        UserAddressListScreen(
            viewState =
                UserAddressListViewState(
                    state = UserAddressListViewState.State.Loading,
                ),
            onAction = {},
        )
    }
}
