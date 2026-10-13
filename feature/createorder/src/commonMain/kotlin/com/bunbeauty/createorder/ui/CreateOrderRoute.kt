package com.bunbeauty.createorder.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bunbeauty.core.Constants.FAB_SNACKBAR_BOTTOM_PADDING
import com.bunbeauty.core.Constants.RUBLE_CURRENCY
import com.bunbeauty.core.motivation.Motivation
import com.bunbeauty.createorder.presentation.createorder.CreateOrder
import com.bunbeauty.createorder.presentation.createorder.CreateOrderViewModel
import com.bunbeauty.createorder.ui.mapper.toViewState
import com.bunbeauty.createorder.ui.ui.DeferredTimeBottomSheet
import com.bunbeauty.createorder.ui.ui.DeliveryAddressListBottomSheet
import com.bunbeauty.createorder.ui.ui.PaymentMethodListBottomSheet
import com.bunbeauty.createorder.ui.ui.PickupAddressListBottomSheet
import com.bunbeauty.createorder.ui.ui.TimePickerDialog
import com.bunbeauty.designsystem.model.PaymentMethodUI
import com.bunbeauty.designsystem.model.PaymentMethodValueUI
import com.bunbeauty.designsystem.theme.FoodDeliveryTheme
import com.bunbeauty.designsystem.theme.bold
import com.bunbeauty.designsystem.ui.LocalBottomBarPadding
import com.bunbeauty.designsystem.ui.element.FoodDeliveryScaffold
import com.bunbeauty.designsystem.ui.element.button.LoadingButton
import com.bunbeauty.designsystem.ui.element.card.BannerCard
import com.bunbeauty.designsystem.ui.element.card.DiscountCard
import com.bunbeauty.designsystem.ui.element.card.FoodDeliveryCard
import com.bunbeauty.designsystem.ui.element.card.FoodDeliveryCardDefaults
import com.bunbeauty.designsystem.ui.element.card.FoodDeliveryCardDefaults.negativeCardStatusColors
import com.bunbeauty.designsystem.ui.element.card.FoodDeliveryCardDefaults.warningCardStatusColors
import com.bunbeauty.designsystem.ui.element.card.FoodDeliveryCheckbox
import com.bunbeauty.designsystem.ui.element.card.NavigationCardWithDivider
import com.bunbeauty.designsystem.ui.element.card.WarningCard
import com.bunbeauty.designsystem.ui.element.footer.WebSiteFooter
import com.bunbeauty.designsystem.ui.element.shimmer.Shimmer
import com.bunbeauty.designsystem.ui.element.surface.FoodDeliverySurface
import com.bunbeauty.designsystem.ui.element.switcher.FoodDeliverySwitcher
import com.bunbeauty.designsystem.ui.element.textfield.FoodDeliveryTextField
import com.bunbeauty.designsystem.ui.element.textfield.FoodDeliveryTextFieldDefaults
import com.bunbeauty.designsystem.ui.getIsImeVisible
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import papakarlo.designsystem.generated.resources.Res
import papakarlo.designsystem.generated.resources.action_create_order_create_order
import papakarlo.designsystem.generated.resources.action_create_order_delivery
import papakarlo.designsystem.generated.resources.action_create_order_pickup
import papakarlo.designsystem.generated.resources.additional_utensils_count
import papakarlo.designsystem.generated.resources.comment
import papakarlo.designsystem.generated.resources.delivery_address
import papakarlo.designsystem.generated.resources.delivery_time
import papakarlo.designsystem.generated.resources.description_ic_warning
import papakarlo.designsystem.generated.resources.error_additional_utensils
import papakarlo.designsystem.generated.resources.error_change
import papakarlo.designsystem.generated.resources.error_enter_correct_amount
import papakarlo.designsystem.generated.resources.error_no_time_available
import papakarlo.designsystem.generated.resources.error_payment_method
import papakarlo.designsystem.generated.resources.error_select_delivery_address
import papakarlo.designsystem.generated.resources.error_select_payment_method
import papakarlo.designsystem.generated.resources.error_something_went_wrong
import papakarlo.designsystem.generated.resources.error_user
import papakarlo.designsystem.generated.resources.error_user_address
import papakarlo.designsystem.generated.resources.hint_change
import papakarlo.designsystem.generated.resources.ic_warning
import papakarlo.designsystem.generated.resources.msg_create_order_amount_to_pay
import papakarlo.designsystem.generated.resources.msg_create_order_average_traffic
import papakarlo.designsystem.generated.resources.msg_create_order_chose_cafe
import papakarlo.designsystem.generated.resources.msg_create_order_high_traffic
import papakarlo.designsystem.generated.resources.msg_delivery
import papakarlo.designsystem.generated.resources.msg_order_code
import papakarlo.designsystem.generated.resources.msg_order_details_discount
import papakarlo.designsystem.generated.resources.msg_without_change
import papakarlo.designsystem.generated.resources.msg_without_utensils
import papakarlo.designsystem.generated.resources.payment_method
import papakarlo.designsystem.generated.resources.pickup_address
import papakarlo.designsystem.generated.resources.title_create_order
import papakarlo.designsystem.generated.resources.warning_no_order_available
import papakarlo.designsystem.generated.resources.warning_no_time_available

@Composable
fun CreateOrder.DataState.mapState(): CreateOrderViewState = toViewState()

@Composable
fun CreateOrderRoute(
    viewModel: CreateOrderViewModel = koinViewModel(),
    back: () -> Unit,
    goToMenu: () -> Unit,
    goToCreateAddress: () -> Unit,
    showInfoMessage: (String, Int) -> Unit,
    showErrorMessage: (String) -> Unit,
) {
    LaunchedEffect(Unit) {
        viewModel.onAction(CreateOrder.Action.Init)
    }

    val viewState by viewModel.dataState.collectAsStateWithLifecycle()
    val onAction =
        remember {
            { event: CreateOrder.Action ->
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

    CreateOrderEffect(
        effects = effects,
        back = back,
        goToMenu = goToMenu,
        goToCreateAddress = goToCreateAddress,
        consumeEffects = consumeEffects,
        showInfoMessage = showInfoMessage,
        showErrorMessage = showErrorMessage,
    )
    CreateOrderScreen(viewState = viewState.mapState(), onAction = onAction)
}

@Composable
private fun CreateOrderScreen(
    viewState: CreateOrderViewState,
    onAction: (CreateOrder.Action) -> Unit,
) {
    FoodDeliveryScaffold(
        title = stringResource(resource = Res.string.title_create_order),
        backActionClick = {
            onAction(CreateOrder.Action.Back)
        },
        backgroundColor = FoodDeliveryTheme.colors.mainColors.surface,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = FoodDeliveryTheme.dimensions.mediumSpace),
            ) {
                val focusManager = LocalFocusManager.current

                FoodDeliverySwitcher(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 8.dp),
                    optionResIdList =
                        persistentListOf(
                            stringResource(Res.string.action_create_order_delivery),
                            stringResource(Res.string.action_create_order_pickup),
                        ),
                    position = viewState.switcherPosition,
                    isLoading = viewState.isLoadingSwitcher,
                    onPositionChanged = { position ->
                        focusManager.clearFocus()
                        onAction(CreateOrder.Action.ChangeMethod(position))
                    },
                )

                when (viewState.createOrderType) {
                    is CreateOrderViewState.CreateOrderType.Delivery -> {
                        DeliveryContent(
                            viewState = viewState,
                            createOrderType = viewState.createOrderType,
                            focusManager = focusManager,
                            onAction = onAction,
                        )
                    }

                    is CreateOrderViewState.CreateOrderType.Pickup -> {
                        PickupContent(
                            viewState = viewState,
                            createOrderType = viewState.createOrderType,
                            focusManager = focusManager,
                            onAction = onAction,
                        )
                    }
                }

                WebSiteFooter(modifier = Modifier.padding(top = 24.dp))
            }

            BottomAmountBar(
                viewState = viewState,
                onAction = onAction,
            )
        }
        DeferredTimeBottomSheet(
            isShown = viewState.isDeferredTimeShown,
            title = stringResource(viewState.deferredTimeStringId),
            onAction = onAction,
        )
        TimePickerDialog(
            timePicker = viewState.timePicker,
            onAction = onAction,
        )
        PaymentMethodListBottomSheet(
            paymentMethodList = viewState.paymentMethodList,
            onAction = onAction,
        )
    }
}

@Composable
fun CreateOrderEffect(
    effects: List<CreateOrder.Event>,
    back: () -> Unit,
    goToMenu: () -> Unit,
    goToCreateAddress: () -> Unit,
    consumeEffects: () -> Unit,
    showInfoMessage: (String, Int) -> Unit,
    showErrorMessage: (String) -> Unit,
) {
    LaunchedEffect(effects) {
        effects.forEach { effect ->
            when (effect) {
                CreateOrder.Event.OpenCreateAddressEvent -> goToCreateAddress()

                CreateOrder.Event.ShowSomethingWentWrongErrorEvent -> {
                    showErrorMessage(getString(Res.string.error_something_went_wrong))
                }

                CreateOrder.Event.ShowUserUnauthorizedErrorEvent -> {
                    showErrorMessage(getString(Res.string.error_user))
                }

                is CreateOrder.Event.OrderCreatedEvent -> {
                    showInfoMessage(
                        getString(
                            Res.string.msg_order_code,
                            effect.code,
                        ),
                        FAB_SNACKBAR_BOTTOM_PADDING,
                    )
                    goToMenu()
                }

                CreateOrder.Event.ShowUserAddressError -> {
                    showErrorMessage(getString(resource = Res.string.error_user_address))
                }

                CreateOrder.Event.ShowPaymentMethodError -> {
                    showErrorMessage(getString(resource = Res.string.error_payment_method))
                }

                CreateOrder.Event.ShowChangeError -> {
                    showErrorMessage(getString(resource = Res.string.error_change))
                }

                CreateOrder.Event.ShowAdditionalUtensilsError -> {
                    showErrorMessage(getString(resource = Res.string.error_additional_utensils))
                }

                CreateOrder.Event.OrderNotAvailableErrorEvent -> {
                    showErrorMessage(getString(resource = Res.string.warning_no_order_available))
                }

                CreateOrder.Event.ShowTimePickerError -> {
                    showErrorMessage(getString(resource = Res.string.error_no_time_available))
                }

                CreateOrder.Event.Back -> back()
            }
        }
        consumeEffects()
    }
}

@Composable
private fun DeliveryContent(
    viewState: CreateOrderViewState,
    createOrderType: CreateOrderViewState.CreateOrderType.Delivery,
    focusManager: FocusManager,
    onAction: (CreateOrder.Action) -> Unit,
) {
    Column {
        DeliveryAddressCard(
            viewState = viewState,
            createOrderType = createOrderType,
            onAction = onAction,
        )
        DeliveryAddressListBottomSheet(
            deliveryAddressList = createOrderType.deliveryAddressList,
            onAction = onAction,
        )

        when (createOrderType.state) {
            CreateOrderViewState.CreateOrderType.Delivery.State.NOT_ENABLED -> {
                WarningCard(
                    title = stringResource(Res.string.warning_no_order_available),
                    icon = Res.drawable.ic_warning,
                    iconDescription = stringResource(Res.string.description_ic_warning),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp),
                    cardColors = negativeCardStatusColors,
                )
            }

            CreateOrderViewState.CreateOrderType.Delivery.State.ENABLED -> {
                if (viewState.isAddressErrorShown) {
                    ErrorText(
                        modifier =
                            Modifier
                                .padding(top = 4.dp)
                                .padding(horizontal = 16.dp),
                        messageStringId = Res.string.error_select_delivery_address,
                    )
                }
                CommonContent(
                    viewState = viewState,
                    focusManager = focusManager,
                    onAction = onAction,
                )
                when (createOrderType.workload) {
                    CreateOrderViewState.CreateOrderType.Delivery.Workload.LOW -> Unit
                    CreateOrderViewState.CreateOrderType.Delivery.Workload.AVERAGE -> {
                        WarningCard(
                            title = stringResource(Res.string.msg_create_order_average_traffic),
                            icon = Res.drawable.ic_warning,
                            iconDescription = stringResource(Res.string.description_ic_warning),
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .padding(top = 16.dp),
                            cardColors = warningCardStatusColors,
                        )
                    }

                    CreateOrderViewState.CreateOrderType.Delivery.Workload.HIGH -> {
                        WarningCard(
                            title = stringResource(Res.string.msg_create_order_high_traffic),
                            icon = Res.drawable.ic_warning,
                            iconDescription = stringResource(Res.string.description_ic_warning),
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .padding(top = 16.dp),
                            cardColors = negativeCardStatusColors,
                        )
                    }
                }
            }

            CreateOrderViewState.CreateOrderType.Delivery.State.NEED_ADDRESS -> {
                WarningCard(
                    title = stringResource(Res.string.error_user_address),
                    icon = Res.drawable.ic_warning,
                    iconDescription = stringResource(Res.string.description_ic_warning),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp),
                    cardColors = warningCardStatusColors,
                )
            }
        }
    }
}

@Composable
private fun DeliveryAddressCard(
    viewState: CreateOrderViewState,
    createOrderType: CreateOrderViewState.CreateOrderType.Delivery,
    onAction: (CreateOrder.Action) -> Unit,
) {
    NavigationCardWithDivider(
        label = stringResource(Res.string.delivery_address),
        value = createOrderType.deliveryAddress,
        clickable = viewState.isFieldsEnabled,
        onClick = {
            if (createOrderType.deliveryAddress == null) {
                onAction(CreateOrder.Action.AddAddressClick)
            } else {
                onAction(CreateOrder.Action.DeliveryAddressClick)
            }
        },
    )
}

@Composable
private fun PickupContent(
    viewState: CreateOrderViewState,
    createOrderType: CreateOrderViewState.CreateOrderType.Pickup,
    focusManager: FocusManager,
    onAction: (CreateOrder.Action) -> Unit,
) {
    Column {
        PickupAddressCard(
            createOrderType = createOrderType,
            viewState = viewState,
            focusManager = focusManager,
            onAction = onAction,
        )
        PickupAddressListBottomSheet(
            pickupAddressList = createOrderType.pickupAddressList,
            onAction = onAction,
        )
        if (createOrderType.isEnabled) {
            CommonContent(
                viewState = viewState,
                focusManager = focusManager,
                onAction = onAction,
            )
        } else {
            if (createOrderType.hasOpenedCafe) {
                BannerCard(
                    title = stringResource(Res.string.msg_create_order_chose_cafe),
                    text = stringResource(Res.string.warning_no_order_available),
                    icon = Res.drawable.ic_warning,
                    iconDescription = stringResource(Res.string.description_ic_warning),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp),
                    cardColors = warningCardStatusColors,
                )
            } else {
                WarningCard(
                    title = stringResource(Res.string.warning_no_order_available),
                    icon = Res.drawable.ic_warning,
                    iconDescription = stringResource(Res.string.description_ic_warning),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp),
                    cardColors = negativeCardStatusColors,
                )
            }
        }
    }
}

@Composable
private fun PickupAddressCard(
    createOrderType: CreateOrderViewState.CreateOrderType.Pickup,
    viewState: CreateOrderViewState,
    focusManager: FocusManager,
    onAction: (CreateOrder.Action) -> Unit,
) {
    NavigationCardWithDivider(
        label = stringResource(resource = Res.string.pickup_address),
        value = createOrderType.pickupAddress.orEmpty(),
        clickable = viewState.isFieldsEnabled,
        onClick = {
            focusManager.clearFocus()
            onAction(CreateOrder.Action.PickupAddressClick)
        },
    )
}

@Composable
private fun CommonContent(
    viewState: CreateOrderViewState,
    focusManager: FocusManager,
    onAction: (CreateOrder.Action) -> Unit,
) {
    if (viewState.isLoadingSwitcher) return

    DeferredTimeCard(
        viewState = viewState,
        focusManager = focusManager,
        onAction = onAction,
    )
    PaymentMethodCard(
        viewState = viewState,
        focusManager = focusManager,
        onAction = onAction,
    )
    Column(modifier = Modifier.padding(top = 16.dp)) {
        ChangeBlock(
            viewState = viewState,
            onAction = onAction,
        )
        WithoutUtensilsBlock(
            viewState = viewState,
            onAction = onAction,
        )
        AdditionalUtensilsTextField(
            viewState = viewState,
            focusManager = focusManager,
            onAction = onAction,
        )
        CommentTextField(
            viewState = viewState,
            focusManager = focusManager,
            onAction = onAction,
        )
    }
}

@Composable
private fun DeferredTimeCard(
    viewState: CreateOrderViewState,
    focusManager: FocusManager,
    onAction: (CreateOrder.Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column {
        NavigationCardWithDivider(
            modifier = modifier,
            label = stringResource(resource = viewState.deferredTimeStringId),
            value = viewState.deferredTime,
            clickable = viewState.isFieldsEnabled,
            onClick = {
                focusManager.clearFocus()
                onAction(CreateOrder.Action.DeferredTimeClick)
            },
        )
        AnimatedVisibility(visible = viewState.showTimePickerHint) {
            Text(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp),
                text = stringResource(resource = Res.string.warning_no_time_available),
                style = FoodDeliveryTheme.typography.bodySmall,
                color =
                    if (viewState.hasTimePickerError) {
                        FoodDeliveryTheme.colors.mainColors.error
                    } else {
                        FoodDeliveryTheme.colors.mainColors.onSurface
                    },
            )
        }
    }
}

@Composable
private fun PaymentMethodCard(
    viewState: CreateOrderViewState,
    focusManager: FocusManager,
    onAction: (CreateOrder.Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationCardWithDivider(
        modifier = modifier,
        label = stringResource(Res.string.payment_method),
        value = viewState.selectedPaymentMethod?.name,
        clickable = viewState.isFieldsEnabled,
        onClick = {
            focusManager.clearFocus()
            onAction(CreateOrder.Action.PaymentMethodClick)
        },
    )

    if (viewState.isPaymentMethodErrorShown) {
        ErrorText(
            modifier =
                Modifier
                    .padding(top = 4.dp)
                    .padding(horizontal = 16.dp),
            messageStringId = Res.string.error_select_payment_method,
        )
    }
}

@Composable
private fun ChangeBlock(
    viewState: CreateOrderViewState,
    onAction: (CreateOrder.Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!viewState.showChange) {
        return
    }

    FoodDeliveryCard(
        modifier = modifier,
        elevated = false,
        shape = FoodDeliveryCardDefaults.zeroCardShape,
        onClick = {
            onAction(
                CreateOrder.Action.ChangeWithoutChangeChecked,
            )
        },
    ) {
        Row(
            modifier =
                Modifier.padding(
                    vertical = 8.dp,
                    horizontal = 16.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FoodDeliveryCheckbox(
                modifier = Modifier.size(24.dp),
                checked = viewState.withoutChangeChecked,
                onCheckedChange = {
                    onAction(
                        CreateOrder.Action.ChangeWithoutChangeChecked,
                    )
                },
            )
            Text(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp),
                text = stringResource(Res.string.msg_without_change),
                style = FoodDeliveryTheme.typography.bodyMedium,
                color = FoodDeliveryTheme.colors.mainColors.onSurface,
            )
        }
    }

    AnimatedVisibility(
        visible = !viewState.withoutChangeChecked,
        enter =
            expandVertically(
                animationSpec = tween(500),
            ),
        exit =
            shrinkVertically(
                animationSpec = tween(500),
            ),
    ) {
        val focusManager = LocalFocusManager.current
        FoodDeliveryTextField(
            modifier =
                Modifier
                    .padding(bottom = 8.dp)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
            value = viewState.change,
            labelStringId = Res.string.hint_change,
            keyboardOptions =
                FoodDeliveryTextFieldDefaults.keyboardOptionsDefault(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
            keyboardActions =
                FoodDeliveryTextFieldDefaults.keyboardActionsDefault(
                    onDone = {
                        focusManager.clearFocus()
                    },
                ),
            onValueChange = { value ->
                onAction(
                    CreateOrder.Action.ChangeChange(change = value),
                )
            },
            maxSymbols = 10,
            errorMessageStringId =
                Res.string.error_enter_correct_amount.takeIf {
                    viewState.isChangeErrorShown
                },
            trailingIcon = {
                Text(
                    text = RUBLE_CURRENCY,
                    style = FoodDeliveryTheme.typography.bodyLarge,
                    color = FoodDeliveryTheme.colors.mainColors.onSurface,
                )
            },
        )
    }
}

@Composable
private fun WithoutUtensilsBlock(
    viewState: CreateOrderViewState,
    onAction: (CreateOrder.Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!viewState.additionalUtensils) return

    FoodDeliveryCard(
        modifier = modifier,
        elevated = false,
        shape = FoodDeliveryCardDefaults.zeroCardShape,
        onClick = {
            onAction(CreateOrder.Action.ChangeWithoutUtensilsChecked)
        },
    ) {
        Row(
            modifier =
                Modifier.padding(
                    vertical = 8.dp,
                    horizontal = 16.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FoodDeliveryCheckbox(
                modifier = Modifier.size(24.dp),
                checked = viewState.withoutUtensilsChecked,
                onCheckedChange = {
                    onAction(CreateOrder.Action.ChangeWithoutUtensilsChecked)
                },
            )
            Text(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp),
                text = stringResource(Res.string.msg_without_utensils),
                style = FoodDeliveryTheme.typography.bodyMedium,
                color = FoodDeliveryTheme.colors.mainColors.onSurface,
            )
        }
    }
}

@Composable
private fun AdditionalUtensilsTextField(
    viewState: CreateOrderViewState,
    focusManager: FocusManager,
    onAction: (CreateOrder.Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!viewState.additionalUtensils) return

    AnimatedVisibility(
        visible = !viewState.withoutUtensilsChecked,
        enter =
            expandVertically(
                animationSpec = tween(500),
            ),
        exit =
            shrinkVertically(
                animationSpec = tween(500),
            ),
    ) {
        FoodDeliveryTextField(
            modifier =
                modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .padding(horizontal = 16.dp),
            value = viewState.additionalUtensilsCount,
            labelStringId = Res.string.additional_utensils_count,
            keyboardOptions =
                FoodDeliveryTextFieldDefaults.keyboardOptionsDefault(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
            onValueChange = { value ->
                onAction(CreateOrder.Action.ChangeAdditionalUtensils(additionalUtensilsCount = value))
            },
            keyboardActions =
                FoodDeliveryTextFieldDefaults.keyboardActionsDefault(
                    onDone = {
                        focusManager.clearFocus()
                    },
                ),
            errorMessageStringId =
                Res.string.error_additional_utensils.takeIf {
                    viewState.isAdditionalUtensilsErrorShown
                },
            maxSymbols = 10,
            maxLines = 1,
        )
    }
}

@Composable
private fun CommentTextField(
    viewState: CreateOrderViewState,
    focusManager: FocusManager,
    onAction: (CreateOrder.Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    FoodDeliveryTextField(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        value = viewState.comment,
        labelStringId = Res.string.comment,
        keyboardOptions =
            FoodDeliveryTextFieldDefaults.keyboardOptionsDefault(
                imeAction = ImeAction.Done,
            ),
        keyboardActions =
            FoodDeliveryTextFieldDefaults.keyboardActionsDefault(
                onDone = {
                    focusManager.clearFocus()
                },
            ),
        onValueChange = { value ->
            onAction(CreateOrder.Action.ChangeComment(comment = value))
        },
        maxSymbols = 100,
        maxLines = 3,
    )
}

@Composable
private fun ErrorText(
    modifier: Modifier = Modifier,
    messageStringId: StringResource,
) {
    Text(
        modifier = modifier,
        text = stringResource(resource = messageStringId),
        style = FoodDeliveryTheme.typography.bodySmall,
        color = FoodDeliveryTheme.colors.mainColors.error,
    )
}

@Composable
private fun BottomAmountBar(
    viewState: CreateOrderViewState,
    onAction: (CreateOrder.Action) -> Unit,
) {
    FoodDeliverySurface(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = spacedBy(16.dp),
        ) {
            when (viewState.cartTotal) {
                CartTotalUI.Loading -> {
                    BottomAmountBarLoadingContent()
                }

                is CartTotalUI.Success -> {
                    BottomAmountBarSuccessContent(cartTotal = viewState.cartTotal)
                }
            }

            LoadingButton(
                modifier =
                    Modifier
                        .padding(
                            bottom =
                                if (getIsImeVisible()) {
                                    0.dp
                                } else {
                                    LocalBottomBarPadding.current
                                },
                        ),
                textStringId = Res.string.action_create_order_create_order,
                isLoading = viewState.isLoadingCreateOrder,
                isEnabled = viewState.isOrderCreationEnabled,
                onClick = {
                    onAction(
                        CreateOrder.Action.CreateClick(
                            withoutChange = viewState.withoutChange,
                            changeFrom = viewState.changeFrom,
                            additionalUtensils = viewState.additionalUtensilsName,
                            withoutUtensils = viewState.withoutUtensils,
                        ),
                    )
                },
            )
        }
    }
}

@Composable
private fun BottomAmountBarLoadingContent() {
    LoadingRow(
        leftWidth = 50.dp,
        rightWidth = 32.dp,
    )
    LoadingRow(
        leftWidth = 64.dp,
        rightWidth = 40.dp,
    )
    LoadingRow(
        leftWidth = 96.dp,
        rightWidth = 80.dp,
    )
}

@Composable
private fun BottomAmountBarSuccessContent(
    cartTotal: CartTotalUI.Success,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = spacedBy(8.dp),
    ) {
        AnimatedVisibility(
            visible = cartTotal.motivation != null,
            enter =
                expandVertically(
                    animationSpec = tween(500),
                ),
            exit =
                shrinkVertically(
                    animationSpec = tween(500),
                ),
        ) {
            Motivation(motivation = cartTotal.motivation)
        }
        cartTotal.discount?.let { discount ->
            Row {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(Res.string.msg_order_details_discount),
                    style = FoodDeliveryTheme.typography.bodyMedium,
                    color = FoodDeliveryTheme.colors.mainColors.onSurface,
                )
                DiscountCard(discount = discount)
            }
        }
        AnimatedVisibility(
            visible = cartTotal.deliveryCost != null,
            enter =
                expandVertically(
                    animationSpec = tween(500),
                ),
            exit =
                shrinkVertically(
                    animationSpec = tween(500),
                ),
        ) {
            Row {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(Res.string.msg_delivery),
                    style = FoodDeliveryTheme.typography.bodyMedium,
                    color = FoodDeliveryTheme.colors.mainColors.onSurface,
                )
                Text(
                    text = cartTotal.deliveryCost.orEmpty(),
                    style = FoodDeliveryTheme.typography.bodyMedium,
                    color = FoodDeliveryTheme.colors.mainColors.onSurface,
                )
            }
        }
        Row {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(Res.string.msg_create_order_amount_to_pay),
                style = FoodDeliveryTheme.typography.bodyMedium.bold,
                color = FoodDeliveryTheme.colors.mainColors.onSurface,
            )
            cartTotal.oldFinalCost?.let { oldFinalCost ->
                Text(
                    modifier = Modifier.padding(end = 4.dp),
                    text = oldFinalCost,
                    style = FoodDeliveryTheme.typography.bodyMedium.bold,
                    color = FoodDeliveryTheme.colors.mainColors.onSurfaceVariant,
                    textDecoration = TextDecoration.LineThrough,
                )
            }
            Text(
                text = cartTotal.newFinalCost,
                style = FoodDeliveryTheme.typography.bodyMedium.bold,
                color = FoodDeliveryTheme.colors.mainColors.onSurface,
            )
        }
    }
}

@Composable
private fun LoadingRow(
    leftWidth: Dp,
    rightWidth: Dp,
) {
    Row {
        val shape = RoundedCornerShape(4.dp)
        Shimmer(
            modifier =
                Modifier
                    .width(leftWidth)
                    .height(20.dp)
                    .clip(shape),
        )
        Spacer(modifier = Modifier.weight(1f))
        Shimmer(
            modifier =
                Modifier
                    .width(rightWidth)
                    .height(20.dp)
                    .clip(shape),
        )
    }
}

private val createOrderViewStatePreviewMock =
    CreateOrderViewState(
        createOrderType =
            CreateOrderViewState.CreateOrderType.Delivery(
                deliveryAddress = "улица Чапаева, д. 22аб кв. 55, 1 подъезд, 1 этаж, код домофона 555",
                deliveryAddressList =
                    DeliveryAddressListUI(
                        isShown = false,
                        addressList = persistentListOf(),
                    ),
                state = CreateOrderViewState.CreateOrderType.Delivery.State.ENABLED,
                workload = CreateOrderViewState.CreateOrderType.Delivery.Workload.LOW,
            ),
        isAddressErrorShown = false,
        comment = "Коммент",
        deferredTime = "Как можно скорее",
        deferredTimeStringId = Res.string.delivery_time,
        selectedPaymentMethod =
            PaymentMethodUI(
                uuid = "",
                name = "Наличка",
                value =
                    PaymentMethodValueUI(
                        value = "Наличка",
                        valueToCopy = "Наличка",
                    ),
            ),
        isPaymentMethodErrorShown = false,
        showChange = true,
        withoutChange = "Без сдачи",
        changeFrom = "Cдача с",
        withoutChangeChecked = true,
        change = "",
        isChangeErrorShown = false,
        cartTotal = CartTotalUI.Loading,
        isLoadingCreateOrder = false,
        isDeferredTimeShown = false,
        timePicker =
            TimePickerUI(
                isShown = false,
                minTime = TimeUI(0, 0),
                initialTime = TimeUI(0, 0),
            ),
        paymentMethodList =
            PaymentMethodListUI(
                isShown = false,
                paymentMethodList = persistentListOf(),
            ),
        isOrderCreationEnabled = false,
        isLoadingSwitcher = false,
        additionalUtensils = false,
        additionalUtensilsCount = "",
        additionalUtensilsName = "Количество приборов",
        withoutUtensils = "",
        withoutUtensilsChecked = false,
        isAdditionalUtensilsErrorShown = false,
        hasTimePickerError = false,
        showTimePickerHint = false,
    )

@Preview(showBackground = true)
@Composable
private fun CartTotalLoadingPreview() {
    FoodDeliveryTheme {
        CreateOrderScreen(
            viewState = createOrderViewStatePreviewMock,
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectedDeliveryPreview() {
    FoodDeliveryTheme {
        CreateOrderScreen(
            viewState =
                createOrderViewStatePreviewMock.copy(
                    isOrderCreationEnabled = true,
                    isLoadingCreateOrder = false,
                    isLoadingSwitcher = false,
                ),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectedPickUpPreview() {
    FoodDeliveryTheme {
        CreateOrderScreen(
            createOrderViewStatePreviewMock.copy(
                createOrderType =
                    CreateOrderViewState.CreateOrderType.Pickup(
                        pickupAddress = "улица Чапаева, д. 22аб кв. 55, 1 подъезд, 1 этаж, код домофона 555",
                        pickupAddressList =
                            PickupAddressListUI(
                                isShown = false,
                                addressList = persistentListOf(),
                            ),
                        hasOpenedCafe = true,
                        isEnabled = true,
                    ),
                isOrderCreationEnabled = true,
                isLoadingCreateOrder = false,
                isLoadingSwitcher = false,
                cartTotal =
                    CartTotalUI.Success(
                        motivation = null,
                        discount = null,
                        deliveryCost = null,
                        oldFinalCost = null,
                        newFinalCost = "650 ₽",
                    ),
            ),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PickupCreateOrderNotEnabledPreview() {
    FoodDeliveryTheme {
        CreateOrderScreen(
            createOrderViewStatePreviewMock.copy(
                createOrderType =
                    CreateOrderViewState.CreateOrderType.Pickup(
                        pickupAddress = "улица Чапаева, д. 22аб кв. 55, 1 подъезд, 1 этаж, код домофона 555",
                        pickupAddressList =
                            PickupAddressListUI(
                                isShown = false,
                                addressList = persistentListOf(),
                            ),
                        hasOpenedCafe = true,
                        isEnabled = false,
                    ),
                isOrderCreationEnabled = false,
                isLoadingCreateOrder = false,
                isLoadingSwitcher = false,
                cartTotal =
                    CartTotalUI.Success(
                        motivation = null,
                        discount = null,
                        deliveryCost = null,
                        oldFinalCost = null,
                        newFinalCost = "650 ₽",
                    ),
            ),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DeliveryCreateOrderNotEnabledPreview() {
    FoodDeliveryTheme {
        CreateOrderScreen(
            createOrderViewStatePreviewMock.copy(
                isOrderCreationEnabled = false,
                isLoadingCreateOrder = false,
                isLoadingSwitcher = false,
                cartTotal =
                    CartTotalUI.Success(
                        motivation = null,
                        discount = null,
                        deliveryCost = null,
                        oldFinalCost = null,
                        newFinalCost = "650 ₽",
                    ),
            ),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DeliveryCreateOrderHighTrafficPreview() {
    FoodDeliveryTheme {
        CreateOrderScreen(
            createOrderViewStatePreviewMock.copy(
                createOrderType =
                    CreateOrderViewState.CreateOrderType.Delivery(
                        deliveryAddress = null,
                        deliveryAddressList =
                            DeliveryAddressListUI(
                                isShown = false,
                                addressList = persistentListOf(),
                            ),
                        state = CreateOrderViewState.CreateOrderType.Delivery.State.ENABLED,
                        workload = CreateOrderViewState.CreateOrderType.Delivery.Workload.HIGH,
                    ),
                isOrderCreationEnabled = false,
                isLoadingCreateOrder = false,
                isLoadingSwitcher = false,
                cartTotal =
                    CartTotalUI.Success(
                        motivation = null,
                        discount = null,
                        deliveryCost = null,
                        oldFinalCost = null,
                        newFinalCost = "650 ₽",
                    ),
            ),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DeliveryCreateOrderNeedAddressPreview() {
    FoodDeliveryTheme {
        CreateOrderScreen(
            createOrderViewStatePreviewMock.copy(
                createOrderType =
                    CreateOrderViewState.CreateOrderType.Delivery(
                        deliveryAddress = null,
                        deliveryAddressList =
                            DeliveryAddressListUI(
                                isShown = false,
                                addressList = persistentListOf(),
                            ),
                        state = CreateOrderViewState.CreateOrderType.Delivery.State.NEED_ADDRESS,
                        workload = CreateOrderViewState.CreateOrderType.Delivery.Workload.HIGH,
                    ),
                isOrderCreationEnabled = false,
                isLoadingCreateOrder = false,
                isLoadingSwitcher = false,
                cartTotal =
                    CartTotalUI.Success(
                        motivation = null,
                        discount = null,
                        deliveryCost = null,
                        oldFinalCost = null,
                        newFinalCost = "650 ₽",
                    ),
            ),
            onAction = {},
        )
    }
}
