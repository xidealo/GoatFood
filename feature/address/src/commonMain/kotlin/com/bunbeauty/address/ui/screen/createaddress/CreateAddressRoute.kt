package com.bunbeauty.address.ui.screen.createaddress

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bunbeauty.address.presentation.create_address.CreateAddress
import com.bunbeauty.address.presentation.create_address.CreateAddressViewModel
import com.bunbeauty.designsystem.theme.FoodDeliveryTheme
import com.bunbeauty.designsystem.ui.LocalBottomBarPadding
import com.bunbeauty.designsystem.ui.element.FoodDeliveryScaffold
import com.bunbeauty.designsystem.ui.element.button.LoadingButton
import com.bunbeauty.designsystem.ui.element.footer.WebStickyFooterColumn
import com.bunbeauty.designsystem.ui.element.textfield.FoodDeliveryTextField
import com.bunbeauty.designsystem.ui.element.textfield.FoodDeliveryTextFieldDefaults
import com.bunbeauty.designsystem.ui.element.textfield.FoodDeliveryTextFieldWithMenu
import com.bunbeauty.designsystem.ui.getIsImeVisible
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import papakarlo.designsystem.generated.resources.Res
import papakarlo.designsystem.generated.resources.action_create_address_save
import papakarlo.designsystem.generated.resources.error_create_address_fail
import papakarlo.designsystem.generated.resources.error_create_address_loading
import papakarlo.designsystem.generated.resources.hint_create_address_comment
import papakarlo.designsystem.generated.resources.hint_create_address_entrance
import papakarlo.designsystem.generated.resources.hint_create_address_flat
import papakarlo.designsystem.generated.resources.hint_create_address_floor
import papakarlo.designsystem.generated.resources.hint_create_address_house
import papakarlo.designsystem.generated.resources.hint_create_address_street
import papakarlo.designsystem.generated.resources.msg_create_address_created
import papakarlo.designsystem.generated.resources.title_create_address

@Composable
fun CreateAddress.DataState.mapState(): CreateAddressViewState = mapCreateAddressState()

@Composable
fun CreateAddressRoute(
    viewModel: CreateAddressViewModel = koinViewModel(),
    back: () -> Unit,
    showInfoMessage: (String, Int) -> Unit,
    showErrorMessage: (String) -> Unit,
) {
    val viewState by viewModel.dataState.collectAsStateWithLifecycle()

    val onAction =
        remember {
            { action: CreateAddress.Action ->
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

    CreateAddressEffect(
        effects = effects,
        back = back,
        consumeEffects = consumeEffects,
        showInfoMessage = showInfoMessage,
        showErrorMessage = showErrorMessage,
    )

    CreateAddressScreen(viewState = viewState.mapState(), onAction = onAction)
}

@Composable
fun CreateAddressEffect(
    effects: List<CreateAddress.Event>,
    back: () -> Unit,
    consumeEffects: () -> Unit,
    showInfoMessage: (String, Int) -> Unit,
    showErrorMessage: (String) -> Unit,
) {
    LaunchedEffect(effects) {
        effects.forEach { effect ->
            when (effect) {
                CreateAddress.Event.SuggestionLoadingFailed -> {
                    showErrorMessage(getString(Res.string.error_create_address_loading))
                }

                CreateAddress.Event.AddressCreatedSuccess -> {
                    showInfoMessage(getString(Res.string.msg_create_address_created), 0)
                    back()
                }

                CreateAddress.Event.AddressCreatedFailed -> {
                    showErrorMessage(getString(Res.string.error_create_address_fail))
                }

                CreateAddress.Event.Back -> back()
            }
        }
        consumeEffects()
    }
}

@Composable
private fun CreateAddressScreen(
    viewState: CreateAddressViewState,
    onAction: (CreateAddress.Action) -> Unit,
) {
    FoodDeliveryScaffold(
        title = stringResource(Res.string.title_create_address),
        backActionClick = {
            onAction(CreateAddress.Action.BackClick)
        },
        actionButton = {
            LoadingButton(
                modifier =
                    Modifier
                        .padding(horizontal = 16.dp)
                        .padding(
                            bottom =
                                if (getIsImeVisible()) {
                                    0.dp
                                } else {
                                    LocalBottomBarPadding.current
                                },
                        ),
                textStringId = Res.string.action_create_address_save,
                isLoading = viewState.isCreateLoading,
                onClick = {
                    onAction(CreateAddress.Action.SaveClick)
                },
            )
        },
        backgroundColor = FoodDeliveryTheme.colors.mainColors.surface,
    ) {
        WebStickyFooterColumn(
            footerModifier =
                Modifier.padding(bottom = FoodDeliveryTheme.dimensions.scrollScreenBottomSpace),
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                val focusManager = LocalFocusManager.current
                var expanded by remember(viewState.suggestionListNotEmpty) {
                    mutableStateOf(viewState.suggestionListNotEmpty)
                }
                FoodDeliveryTextFieldWithMenu(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                onAction(CreateAddress.Action.StreetFocusChange(isFocused = focusState.isFocused))
                                expanded =
                                    focusState.isFocused &&
                                    viewState.suggestionListNotEmpty
                            },
                    expanded = expanded,
                    onExpandedChange = { value ->
                        expanded = value
                    },
                    onSuggestionClick = { suggestion ->
                        focusManager.moveFocus(FocusDirection.Down)
                        onAction(CreateAddress.Action.SuggestionSelect(suggestion = suggestion))
                    },
                    value = viewState.street,
                    labelStringId = Res.string.hint_create_address_street,
                    onValueChange = { street ->
                        onAction(CreateAddress.Action.StreetTextChange(street = street))
                    },
                    errorMessageStringId = viewState.streetErrorStringId,
                    suggestionsList = viewState.streetSuggestionList,
                    isLoading = viewState.isSuggestionLoading,
                )

                FoodDeliveryTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewState.house,
                    labelStringId = Res.string.hint_create_address_house,
                    onValueChange = { value ->
                        onAction(CreateAddress.Action.HouseTextChange(house = value))
                    },
                    maxSymbols = 5,
                    errorMessageStringId = viewState.houseErrorStringId,
                )

                FoodDeliveryTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewState.flat,
                    labelStringId = Res.string.hint_create_address_flat,
                    onValueChange = { value ->
                        onAction(CreateAddress.Action.FlatTextChange(flat = value))
                    },
                    maxSymbols = 5,
                )

                FoodDeliveryTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewState.entrance,
                    labelStringId = Res.string.hint_create_address_entrance,
                    onValueChange = { value ->
                        onAction(CreateAddress.Action.EntranceTextChange(entrance = value))
                    },
                    maxSymbols = 5,
                )

                FoodDeliveryTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewState.floor,
                    labelStringId = Res.string.hint_create_address_floor,
                    onValueChange = { value ->
                        onAction(CreateAddress.Action.FloorTextChange(floor = value))
                    },
                    maxSymbols = 5,
                )

                FoodDeliveryTextField(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                    value = viewState.comment,
                    labelStringId = Res.string.hint_create_address_comment,
                    keyboardOptions =
                        FoodDeliveryTextFieldDefaults.keyboardOptionsDefault(
                            imeAction = ImeAction.Done,
                        ),
                    maxLines = 5,
                    onValueChange = { value ->
                        onAction(CreateAddress.Action.CommentTextChange(comment = value))
                    },
                    maxSymbols = 100,
                )
            }
        }
    }
}

@Preview
@Composable
private fun CreateAddressScreenPreview() {
    FoodDeliveryTheme {
        CreateAddressScreen(
            viewState =
                CreateAddressViewState(
                    street = "Street",
                    streetErrorStringId = null,
                    streetSuggestionList = persistentListOf(),
                    isSuggestionLoading = true,
                    house = "1",
                    houseErrorStringId = null,
                    flat = "",
                    entrance = "",
                    floor = "",
                    comment = "",
                    isCreateLoading = false,
                ),
            onAction = {},
        )
    }
}
