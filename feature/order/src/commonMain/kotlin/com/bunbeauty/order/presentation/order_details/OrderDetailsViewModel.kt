package com.bunbeauty.order.presentation.order_details

import com.bunbeauty.core.Constants.PERCENT
import com.bunbeauty.core.Constants.RUBLE_CURRENCY
import com.bunbeauty.core.base.SharedStateViewModel
import com.bunbeauty.core.domain.order.ObserveOrderUseCase
import com.bunbeauty.core.domain.order.StopObserveOrdersUseCase
import com.bunbeauty.core.extension.launchSafe
import com.bunbeauty.core.model.order.Order
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest

class OrderDetailsViewModel(
    private val observeOrderUseCase: ObserveOrderUseCase,
    private val stopObserveOrdersUseCase: StopObserveOrdersUseCase,
) : SharedStateViewModel<OrderDetails.DataState, OrderDetails.Action, OrderDetails.Event>(
        OrderDetails.DataState(
            orderDetailsData =
                OrderDetails.DataState.OrderDetailsData(
                    orderProductItemList = listOf(),
                    orderInfo = null,
                    deliveryCost = null,
                    newTotalCost = "",
                    discount = null,
                ),
            screenState = OrderDetails.DataState.ScreenState.LOADING,
            orderUuid = "",
        ),
    ) {
    private var observeOrderJob: Job? = null
    private var orderObservationUuid: String? = null

    override fun reduce(
        action: OrderDetails.Action,
        dataState: OrderDetails.DataState,
    ) {
        when (action) {
            OrderDetails.Action.Back ->
                addEvent {
                    OrderDetails.Event.Back
                }

            is OrderDetails.Action.StartObserve -> startObserveOrder(action.orderUuid)
            is OrderDetails.Action.Reload -> reloadOrder(action.orderUuid)
            OrderDetails.Action.StopObserve -> stopObserveOrders()
        }
    }

    private fun reloadOrder(orderUuid: String) {
        setState {
            copy(
                screenState = OrderDetails.DataState.ScreenState.LOADING,
            )
        }
        stopObserveOrders()
        startObserveOrder(orderUuid = orderUuid)
    }

    private fun startObserveOrder(orderUuid: String) {
        observeOrderJob =
            sharedScope.launchSafe(
                block = {
                    val (uuid, orderFlow) = observeOrderUseCase(orderUuid)
                    orderObservationUuid = uuid
                    orderFlow.collectLatest { order ->
                        if (order != null) {
                            setState {
                                copy(
                                    orderUuid = orderUuid,
                                    orderDetailsData =
                                        OrderDetails.DataState.OrderDetailsData(
                                            orderProductItemList = getProductList(order),
                                            orderInfo = getOrderInfo(order),
                                            deliveryCost =
                                                order.deliveryCost?.let { deliveryCost ->
                                                    "$deliveryCost $RUBLE_CURRENCY"
                                                },
                                            newTotalCost =
                                                order.newTotalCost.let { newTotalCost ->
                                                    "$newTotalCost $RUBLE_CURRENCY"
                                                },
                                            discount =
                                                order.percentDiscount?.let { discount ->
                                                    "$discount$PERCENT"
                                                },
                                        ),
                                    screenState = OrderDetails.DataState.ScreenState.SUCCESS,
                                )
                            }
                        }
                    }
                },
                onError = {
                    setState {
                        copy(
                            orderUuid = orderUuid,
                            screenState = OrderDetails.DataState.ScreenState.ERROR,
                        )
                    }
                },
            )
    }

    private fun stopObserveOrders() {
        observeOrderJob?.cancel()
        orderObservationUuid?.let { uuid ->
            sharedScope.launchSafe(
                block = {
                    stopObserveOrdersUseCase(uuid)
                },
                onError = {
                },
            )
        }
        orderObservationUuid = null
    }

    private fun getOrderInfo(order: Order) =
        OrderDetails.DataState.OrderDetailsData.OrderInfo(
            code = order.code,
            status = order.status,
            dateTime = order.dateTime,
            deferredTime = order.deferredTime,
            address = order.address,
            comment = order.comment,
            isDelivery = order.isDelivery,
            paymentMethod = order.paymentMethod,
            pickupServing = order.pickupServing,
        )

    private fun getProductList(order: Order) =
        order.orderProductList.mapIndexed { index, orderProduct ->
            OrderDetails.DataState.OrderDetailsData.OrderProductItem(
                uuid = orderProduct.uuid,
                name = orderProduct.product.name,
                newPrice = "${orderProduct.product.newCommonPrice} $RUBLE_CURRENCY",
                newCost = "${orderProduct.product.newTotalCost} $RUBLE_CURRENCY",
                photoLink = orderProduct.product.photoLink,
                count = orderProduct.count.toString(),
                additions = orderProduct.orderAdditionList,
                isLast = index == order.orderProductList.lastIndex,
            )
        }
}
