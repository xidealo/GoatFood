package com.bunbeauty.order.presentation.order_details

import com.bunbeauty.core.base.BaseAction
import com.bunbeauty.core.base.BaseDataState
import com.bunbeauty.core.base.BaseEvent
import com.bunbeauty.core.model.addition.OrderAddition
import com.bunbeauty.core.model.date_time.DateTime
import com.bunbeauty.core.model.date_time.Time
import com.bunbeauty.core.model.order.OrderAddress
import com.bunbeauty.core.model.order.OrderStatus
import com.bunbeauty.core.model.order.PickupServing
import com.bunbeauty.core.model.payment_method.PaymentMethodName

interface OrderDetails {
    data class DataState(
        val orderUuid: String,
        val orderDetailsData: OrderDetailsData,
        val screenState: ScreenState,
    ) : BaseDataState {
        data class OrderDetailsData(
            val orderProductItemList: List<OrderProductItem>,
            val orderInfo: OrderInfo?,
            val deliveryCost: String?,
            val newTotalCost: String,
            val discount: String?,
        ) {
            data class OrderInfo(
                val code: String,
                val status: OrderStatus,
                val dateTime: DateTime,
                val deferredTime: Time?,
                val address: OrderAddress,
                val comment: String?,
                val isDelivery: Boolean,
                val paymentMethod: PaymentMethodName?,
                val pickupServing: PickupServing?,
            )

            data class OrderProductItem(
                val uuid: String,
                val name: String,
                val newPrice: String,
                val newCost: String,
                val photoLink: String,
                val count: String,
                val additions: List<OrderAddition>,
                val isLast: Boolean,
            )
        }

        enum class ScreenState {
            LOADING,
            SUCCESS,
            ERROR,
        }
    }

    sealed interface Action : BaseAction {
        data object Back : Action

        data class StartObserve(
            val orderUuid: String,
        ) : Action

        data class Reload(
            val orderUuid: String,
        ) : Action

        data object StopObserve : Action
    }

    sealed interface Event : BaseEvent {
        data object Back : Event
    }
}
