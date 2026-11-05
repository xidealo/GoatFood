package com.bunbeauty.order.ui.screen.orderdetails

import androidx.compose.runtime.Composable
import com.bunbeauty.core.extension.getDateTimeString
import com.bunbeauty.core.extension.getDeferredString
import com.bunbeauty.core.extension.getOrderStatusName
import com.bunbeauty.core.extension.getPickupMethodString
import com.bunbeauty.core.extension.getTimeString
import com.bunbeauty.core.extension.mapToString
import com.bunbeauty.core.model.order.PickupServing
import com.bunbeauty.order.presentation.order_details.OrderDetails
import org.jetbrains.compose.resources.stringResource
import papakarlo.designsystem.generated.resources.Res
import papakarlo.designsystem.generated.resources.asap
import papakarlo.designsystem.generated.resources.pickup_serving_bag
import papakarlo.designsystem.generated.resources.pickup_serving_tray

@Composable
fun OrderDetails.DataState.mapToOrderDetailsViewState() =
    OrderDetailsViewState(
        orderProductItemList =
            orderDetailsData.orderProductItemList.map { orderProductItem ->
                orderProductItem.toItem()
            },
        deliveryCost = orderDetailsData.deliveryCost,
        newTotalCost = orderDetailsData.newTotalCost,
        orderInfo =
            orderDetailsData.orderInfo?.let { orderInfo ->
                OrderDetailsViewState.OrderInfo(
                    status = orderInfo.status,
                    statusName = orderInfo.status.getOrderStatusName(),
                    dateTime = orderInfo.dateTime.getDateTimeString(),
                    deferredTime =
                        orderInfo.deferredTime?.getTimeString()
                            ?: stringResource(resource = Res.string.asap),
                    address = orderInfo.address.getOrderAddressString(),
                    comment = orderInfo.comment,
                    pickupMethod = getPickupMethodString(orderInfo.isDelivery),
                    deferredTimeHint = getDeferredString(orderInfo.isDelivery),
                    paymentMethod = orderInfo.paymentMethod?.mapToString(),
                    pickupServing = pickupServingText(pickupServing = orderInfo.pickupServing),
                )
            },
        code = orderDetailsData.orderInfo?.code.orEmpty(),
        discount = orderDetailsData.discount,
        state = screenState,
        orderUuid = orderUuid,
    )

@Composable
private fun pickupServingText(pickupServing: PickupServing?): String? =
    when (pickupServing) {
        PickupServing.TRAY -> stringResource(Res.string.pickup_serving_tray)
        PickupServing.BAG -> stringResource(Res.string.pickup_serving_bag)
        null -> null
    }
