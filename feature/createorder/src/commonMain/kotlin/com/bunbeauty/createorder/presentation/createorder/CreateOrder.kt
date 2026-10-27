package com.bunbeauty.createorder.presentation.createorder

import com.bunbeauty.core.base.BaseAction
import com.bunbeauty.core.base.BaseDataState
import com.bunbeauty.core.base.BaseEvent
import com.bunbeauty.core.model.address.SelectableUserAddress
import com.bunbeauty.core.model.address.UserAddress
import com.bunbeauty.core.model.cafe.Cafe
import com.bunbeauty.core.model.cafe.SelectableCafe
import com.bunbeauty.core.model.date_time.Time
import com.bunbeauty.core.model.order.PickupServing
import com.bunbeauty.core.model.payment_method.PaymentMethod
import com.bunbeauty.core.model.payment_method.PaymentMethodName
import com.bunbeauty.core.model.payment_method.SelectablePaymentMethod
import com.bunbeauty.core.motivation.MotivationData

interface CreateOrder {
    data class DataState(
        val isDelivery: Boolean = true,
        val userAddressList: List<SelectableUserAddress> = emptyList(),
        val selectedUserAddress: UserAddress? = null,
        val isUserAddressListShown: Boolean = false,
        val isAddressErrorShown: AddressErrorState = AddressErrorState.INIT,
        val cafeList: List<SelectableCafe> = emptyList(),
        val isCafeListShown: Boolean = false,
        val selectedCafe: Cafe? = null,
        val deferredTime: DeferredTime = DeferredTime.Asap,
        val isDeferredTimeShown: Boolean = false,
        val isTimePickerShown: Boolean = false,
        val minDeferredTime: Time =
            Time(
                hours = 0,
                minutes = 0,
            ),
        val initialDeferredTime: Time =
            Time(
                hours = 0,
                minutes = 0,
            ),
        val hasTimePickerError: Boolean = false,
        val showTimePickerHint: Boolean = false,
        val paymentMethodList: List<SelectablePaymentMethod> = emptyList(),
        val isPaymentMethodListShown: Boolean = false,
        val selectedPaymentMethod: PaymentMethod? = null,
        val isPaymentMethodErrorShown: Boolean = false,
        val selectedPickupServing: PickupServing = PickupServing.BAG,
        val isPickupServingListShown: Boolean = false,
        val withoutChangeChecked: Boolean = false,
        val change: Int? = null,
        val isChangeErrorShown: Boolean = false,
        val isAdditionalUtensilsErrorShown: Boolean = false,
        val withoutUtensilsChecked: Boolean = false,
        val comment: String = "",
        val cartTotal: CartTotal,
        val isLoading: Boolean,
        val isLoadingSwitcher: Boolean = true,
        val isPickupEnabled: Boolean,
        val deliveryState: DeliveryState,
        val hasOpenedCafe: Boolean,
        val workload: Cafe.Workload,
        val additionalUtensilsCount: String,
        val additionalUtensils: Boolean,
    ) : BaseDataState {
        val paymentByCash: Boolean = selectedPaymentMethod?.name == PaymentMethodName.CASH

        enum class AddressErrorState {
            INIT,
            NO_ERROR,
            ERROR,
        }

        enum class DeliveryState {
            NOT_ENABLED,
            ENABLED,
            NEED_ADDRESS,
        }
    }

    sealed interface DeferredTime {
        data object Asap : DeferredTime

        data class Later(
            val time: Time,
        ) : DeferredTime
    }

    sealed interface CartTotal {
        data object Loading : CartTotal

        data class Success(
            val motivation: MotivationData?,
            val discount: String?,
            val deliveryCost: String?,
            val oldFinalCost: String?,
            val newFinalCost: String,
            val newFinalCostValue: Int,
        ) : CartTotal
    }

    sealed interface Action : BaseAction {
        data object Init : Action

        data object Back : Action

        data class ChangeMethod(
            val position: Int,
        ) : Action

        data object DeliveryAddressClick : Action

        data object AddAddressClick : Action

        data object HideDeliveryAddressList : Action

        data class ChangeDeliveryAddress(
            val addressUuid: String,
        ) : Action

        data object PickupAddressClick : Action

        data object HidePickupAddressList : Action

        data class ChangePickupAddress(
            val addressUuid: String,
        ) : Action

        data object DeferredTimeClick : Action

        data object HideDeferredTime : Action

        data object AsapClick : Action

        data object PickTimeClick : Action

        data object HideTimePicker : Action

        data class ChangeDeferredTime(
            val time: Time,
        ) : Action

        data object PaymentMethodClick : Action

        data object HidePaymentMethodList : Action

        data class ChangePaymentMethod(
            val paymentMethodUuid: String,
        ) : Action

        data object PickupServingClick : Action

        data object HidePickupServingList : Action

        data class ChangePickupServing(
            val pickupServing: PickupServing,
        ) : Action

        data object ChangeWithoutChangeChecked : Action

        data class ChangeChange(
            val change: String,
        ) : Action

        data class ChangeComment(
            val comment: String,
        ) : Action

        data class ChangeAdditionalUtensils(
            val additionalUtensilsCount: String,
        ) : Action

        data object ChangeWithoutUtensilsChecked : Action

        data class CreateClick(
            val withoutChange: String,
            val changeFrom: String,
            val additionalUtensils: String,
            val withoutUtensils: String,
        ) : Action
    }

    sealed interface Event : BaseEvent {
        data object OpenCreateAddressEvent : Event

        data object ShowUserUnauthorizedErrorEvent : Event

        data object ShowSomethingWentWrongErrorEvent : Event

        data object OrderNotAvailableErrorEvent : Event

        data class OrderCreatedEvent(
            val code: String,
        ) : Event

        data object ShowUserAddressError : Event

        data object ShowPaymentMethodError : Event

        data object ShowChangeError : Event

        data object ShowTimePickerError : Event

        data object ShowAdditionalUtensilsError : Event

        data object Back : Event
    }
}
