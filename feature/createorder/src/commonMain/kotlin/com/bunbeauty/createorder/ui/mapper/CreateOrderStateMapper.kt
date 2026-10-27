package com.bunbeauty.createorder.ui.mapper

import androidx.compose.runtime.Composable
import com.bunbeauty.core.extension.toAddressString
import com.bunbeauty.core.model.cafe.Cafe
import com.bunbeauty.core.model.order.PickupServing
import com.bunbeauty.core.motivation.MotivationUi
import com.bunbeauty.core.motivation.toMotivationUi
import com.bunbeauty.createorder.presentation.createorder.CreateOrder
import com.bunbeauty.createorder.ui.CartTotalUI
import com.bunbeauty.createorder.ui.CreateOrderViewState
import com.bunbeauty.createorder.ui.DeliveryAddressListUI
import com.bunbeauty.createorder.ui.PaymentMethodListUI
import com.bunbeauty.createorder.ui.PickupAddressListUI
import com.bunbeauty.createorder.ui.PickupServingListUI
import com.bunbeauty.createorder.ui.SelectableAddressUI
import com.bunbeauty.createorder.ui.SelectablePickupServingUI
import com.bunbeauty.createorder.ui.TimePickerUI
import com.bunbeauty.createorder.ui.deferredtime.toDeferredTimeString
import com.bunbeauty.createorder.ui.deferredtime.toTimeUI
import com.bunbeauty.createorder.ui.paymentmethod.toPaymentMethodUI
import com.bunbeauty.createorder.ui.paymentmethod.toSelectablePaymentMethodUI
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource
import papakarlo.designsystem.generated.resources.Res
import papakarlo.designsystem.generated.resources.delivery_time
import papakarlo.designsystem.generated.resources.msg_additional_utensils_count
import papakarlo.designsystem.generated.resources.msg_change_from
import papakarlo.designsystem.generated.resources.msg_without_change
import papakarlo.designsystem.generated.resources.msg_without_utensils
import papakarlo.designsystem.generated.resources.pickup_serving_bag
import papakarlo.designsystem.generated.resources.pickup_serving_tray
import papakarlo.designsystem.generated.resources.pickup_time

@Composable
fun CreateOrder.DataState.toViewState(): CreateOrderViewState {
    val cartTotalUI = cartTotal.toCartTotalUI()
    return CreateOrderViewState(
        createOrderType =
            if (isDelivery) {
                getCreateOrderTypeDelivery()
            } else {
                getCreateOrderTypePickup()
            },
        isAddressErrorShown = isDelivery && (isAddressErrorShown == CreateOrder.DataState.AddressErrorState.ERROR),
        comment = comment,
        deferredTimeStringId =
            if (isDelivery) {
                Res.string.delivery_time
            } else {
                Res.string.pickup_time
            },
        hasTimePickerError = hasTimePickerError,
        showTimePickerHint = showTimePickerHint,
        deferredTime = deferredTime.toDeferredTimeString(),
        selectedPaymentMethod = selectedPaymentMethod?.toPaymentMethodUI(),
        isPaymentMethodErrorShown = isPaymentMethodErrorShown,
        showChange = paymentByCash,
        withoutChange = stringResource(Res.string.msg_without_change),
        changeFrom = stringResource(Res.string.msg_change_from),
        withoutChangeChecked = withoutChangeChecked,
        change = change?.toString().orEmpty(),
        isChangeErrorShown = isChangeErrorShown,
        isAdditionalUtensilsErrorShown = isAdditionalUtensilsErrorShown,
        cartTotal = cartTotalUI,
        isLoadingCreateOrder = isLoading,
        isDeferredTimeShown = isDeferredTimeShown,
        timePicker =
            TimePickerUI(
                isShown = isTimePickerShown,
                minTime = minDeferredTime.toTimeUI(),
                initialTime = initialDeferredTime.toTimeUI(),
            ),
        paymentMethodList =
            PaymentMethodListUI(
                isShown = isPaymentMethodListShown,
                paymentMethodList =
                    paymentMethodList
                        .map { selectablePaymentMethod ->
                            selectablePaymentMethod.toSelectablePaymentMethodUI()
                        }.toImmutableList(),
            ),
        isOrderCreationEnabled =
            if (isDelivery) {
                deliveryState == CreateOrder.DataState.DeliveryState.ENABLED &&
                    (cartTotalUI as? CartTotalUI.Success)?.motivation !is MotivationUi.MinOrderCost
            } else {
                isPickupEnabled
            },
        isLoadingSwitcher = isLoadingSwitcher,
        additionalUtensils = additionalUtensils,
        additionalUtensilsCount = additionalUtensilsCount,
        additionalUtensilsName = stringResource(Res.string.msg_additional_utensils_count),
        withoutUtensils = stringResource(Res.string.msg_without_utensils),
        withoutUtensilsChecked = withoutUtensilsChecked,
    )
}

@Composable
private fun CreateOrder.DataState.getCreateOrderTypePickup() =
    CreateOrderViewState.CreateOrderType.Pickup(
        pickupAddress = selectedCafe?.address,
        pickupAddressList =
            PickupAddressListUI(
                isShown = isCafeListShown,
                addressList =
                    cafeList
                        .map { selectableCafe ->
                            SelectableAddressUI(
                                uuid = selectableCafe.cafe.uuid,
                                address = selectableCafe.cafe.address,
                                isSelected = selectableCafe.isSelected,
                                isEnabled = selectableCafe.canBePickup,
                            )
                        }.toImmutableList(),
            ),
        hasOpenedCafe = hasOpenedCafe,
        isEnabled = isPickupEnabled,
        selectedPickupServing = selectedPickupServing.toName(),
        pickupServingList =
            PickupServingListUI(
                isShown = isPickupServingListShown,
                pickupServingList =
                    PickupServing.entries
                        .map { pickupServing ->
                            SelectablePickupServingUI(
                                pickupServing = pickupServing,
                                name = pickupServing.toName(),
                                isSelected = pickupServing == selectedPickupServing,
                            )
                        }.toImmutableList(),
            ),
    )

@Composable
private fun PickupServing.toName(): String =
    when (this) {
        PickupServing.TRAY -> stringResource(Res.string.pickup_serving_tray)
        PickupServing.BAG -> stringResource(Res.string.pickup_serving_bag)
    }

@Composable
private fun CreateOrder.DataState.getCreateOrderTypeDelivery() =
    CreateOrderViewState.CreateOrderType.Delivery(
        deliveryAddress = selectedUserAddress?.toAddressString(),
        deliveryAddressList =
            DeliveryAddressListUI(
                isShown = isUserAddressListShown,
                addressList =
                    userAddressList
                        .map { selectableUserAddress ->
                            SelectableAddressUI(
                                uuid = selectableUserAddress.address.uuid,
                                address = selectableUserAddress.address.toAddressString(),
                                isSelected = selectableUserAddress.isSelected,
                                isEnabled = true,
                            )
                        }.toImmutableList(),
            ),
        state =
            when (deliveryState) {
                CreateOrder.DataState.DeliveryState.NOT_ENABLED -> {
                    CreateOrderViewState.CreateOrderType.Delivery.State.NOT_ENABLED
                }

                CreateOrder.DataState.DeliveryState.ENABLED -> {
                    CreateOrderViewState.CreateOrderType.Delivery.State.ENABLED
                }

                CreateOrder.DataState.DeliveryState.NEED_ADDRESS -> {
                    CreateOrderViewState.CreateOrderType.Delivery.State.NEED_ADDRESS
                }
            },
        workload =
            when (workload) {
                Cafe.Workload.LOW -> CreateOrderViewState.CreateOrderType.Delivery.Workload.LOW
                Cafe.Workload.AVERAGE -> CreateOrderViewState.CreateOrderType.Delivery.Workload.AVERAGE
                Cafe.Workload.HIGH -> CreateOrderViewState.CreateOrderType.Delivery.Workload.HIGH
            },
    )

private fun CreateOrder.CartTotal.toCartTotalUI(): CartTotalUI =
    when (this) {
        CreateOrder.CartTotal.Loading -> CartTotalUI.Loading
        is CreateOrder.CartTotal.Success -> {
            CartTotalUI.Success(
                motivation = motivation?.toMotivationUi(),
                discount = discount,
                deliveryCost = deliveryCost,
                oldFinalCost = oldFinalCost,
                newFinalCost = newFinalCost,
            )
        }
    }
