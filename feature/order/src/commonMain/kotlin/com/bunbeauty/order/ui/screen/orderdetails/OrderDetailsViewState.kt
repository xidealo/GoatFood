package com.bunbeauty.order.ui.screen.orderdetails

import com.bunbeauty.core.base.BaseViewState
import com.bunbeauty.core.model.order.OrderStatus
import com.bunbeauty.order.presentation.order_details.OrderDetails

data class OrderDetailsViewState(
    val orderUuid: String,
    val orderProductItemList: List<OrderProductUiItem>,
    val deliveryCost: String?,
    val newTotalCost: String,
    val state: OrderDetails.DataState.ScreenState,
    val code: String,
    val orderInfo: OrderInfo?,
    val discount: String?,
) : BaseViewState {
    data class OrderInfo(
        val status: OrderStatus,
        val statusName: String,
        val dateTime: String,
        val deferredTime: String?,
        val address: String,
        val comment: String?,
        val pickupMethod: String,
        val deferredTimeHint: String,
        val paymentMethod: String?,
        val pickupServing: String?,
    )
}
