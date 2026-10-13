package com.bunbeauty.order.ui.screen.orderlist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bunbeauty.core.extension.getOrderColor
import com.bunbeauty.core.model.order.OrderStatus
import com.bunbeauty.designsystem.theme.FoodDeliveryTheme
import com.bunbeauty.designsystem.ui.LocalBottomBarPadding
import com.bunbeauty.designsystem.ui.element.FoodDeliveryScaffold
import com.bunbeauty.designsystem.ui.element.card.FoodDeliveryItem
import com.bunbeauty.designsystem.ui.element.footer.WebStickyFooterLazyColumn
import com.bunbeauty.designsystem.ui.screen.EmptyScreen
import com.bunbeauty.designsystem.ui.screen.ErrorScreen
import com.bunbeauty.designsystem.ui.screen.LoadingScreen
import com.bunbeauty.order.presentation.order_list.OrderListState
import com.bunbeauty.order.presentation.order_list.OrderListViewModel
import com.bunbeauty.order.ui.model.OrderItem
import com.bunbeauty.order.ui.toItem
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import papakarlo.designsystem.generated.resources.Res
import papakarlo.designsystem.generated.resources.description_cafe_addresses_empty
import papakarlo.designsystem.generated.resources.error_order_list_loading
import papakarlo.designsystem.generated.resources.ic_history
import papakarlo.designsystem.generated.resources.msg_order_list_empty
import papakarlo.designsystem.generated.resources.title_my_orders
import papakarlo.designsystem.generated.resources.title_order_list_empty

@Composable
fun OrderListState.DataState.mapState(): OrderListViewState =
    OrderListViewState(
        state =
            when (state) {
                OrderListState.DataState.State.SUCCESS -> OrderListViewState.State.Success
                OrderListState.DataState.State.LOADING -> OrderListViewState.State.Loading
                OrderListState.DataState.State.ERROR -> OrderListViewState.State.Error
                OrderListState.DataState.State.EMPTY -> OrderListViewState.State.Empty
            },
        orderList =
            orderList.mapIndexed { index, lightOrder ->
                lightOrder.toItem(isLast = index == orderList.lastIndex)
            },
    )

@Composable
fun OrderListRoute(
    viewModel: OrderListViewModel = koinViewModel(),
    back: () -> Unit,
    goToOrderDetails: (String) -> Unit,
) {
    val viewState by viewModel.dataState.collectAsStateWithLifecycle()

    val onAction =
        remember {
            { event: OrderListState.Action ->
                viewModel.onAction(event)
            }
        }

    LifecycleStartEffect(Unit) {
        viewModel.onAction(OrderListState.Action.StartObserveOrder)
        onStopOrDispose {
            viewModel.onAction(OrderListState.Action.StopObserveOrder)
        }
    }

    val effects by viewModel.events.collectAsStateWithLifecycle()

    val consumeEffects =
        remember {
            {
                viewModel.consumeEvents(effects)
            }
        }

    OrderListEffect(
        effectList = effects,
        goToOrderDetails = goToOrderDetails,
        consumeEffects = consumeEffects,
        back = back,
    )

    OrderListScreen(
        onAction = onAction,
        viewState = viewState.mapState(),
    )
}

@Composable
private fun OrderListScreen(
    onAction: (OrderListState.Action) -> Unit,
    viewState: OrderListViewState,
) {
    FoodDeliveryScaffold(
        title = stringResource(resource = Res.string.title_my_orders),
        backgroundColor = FoodDeliveryTheme.colors.mainColors.surface,
        backActionClick = { onAction(OrderListState.Action.BackClicked) },
    ) {
        when (viewState.state) {
            OrderListViewState.State.Success -> {
                OrderListScreenSuccess(
                    orderItemList = viewState.orderList,
                    onAction = onAction,
                )
            }

            OrderListViewState.State.Loading -> {
                LoadingScreen()
            }

            OrderListViewState.State.Error ->
                ErrorScreen(
                    mainTextId = Res.string.error_order_list_loading,
                    onClick = {
                        onAction(OrderListState.Action.OnRefreshClicked)
                    },
                )

            OrderListViewState.State.Empty ->
                EmptyScreen(
                    imageId = Res.drawable.ic_history,
                    imageDescriptionId = Res.string.description_cafe_addresses_empty,
                    mainTextId = Res.string.title_order_list_empty,
                    extraTextId = Res.string.msg_order_list_empty,
                )
        }
    }
}

@Composable
private fun OrderListScreenSuccess(
    orderItemList: List<OrderItem>,
    onAction: (OrderListState.Action) -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize(),
    ) {
        WebStickyFooterLazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding =
                PaddingValues(
                    top = 8.dp,
                    bottom = LocalBottomBarPadding.current + 8.dp,
                ),
        ) {
            items(orderItemList) { orderItem ->
                FoodDeliveryItem(needDivider = !orderItem.isLast) {
                    com.bunbeauty.order.ui.ui.OrderItem(
                        orderItem = orderItem,
                        onClick = {
                            onAction(OrderListState.Action.OnOrderClicked(orderItem.uuid))
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderListEffect(
    effectList: List<OrderListState.Event>,
    goToOrderDetails: (String) -> Unit,
    consumeEffects: () -> Unit,
    back: () -> Unit,
) {
    LaunchedEffect(effectList) {
        effectList.forEach { event ->
            when (event) {
                is OrderListState.Event.OpenOrderDetailsEvent -> {
                    goToOrderDetails(event.orderUuid)
                }

                OrderListState.Event.GoBackEvent -> back()
            }
            consumeEffects()
        }
    }
}

val orderListViewStateMock =
    OrderListViewState(
        state = OrderListViewState.State.Loading,
        orderList = emptyList(),
    )

@Preview(showBackground = true)
@Composable
private fun OrderListScreenSuccessPreview() {
    FoodDeliveryTheme {
        OrderListScreen(
            viewState =
                orderListViewStateMock.copy(
                    state = OrderListViewState.State.Success,
                    orderList =
                        listOf(
                            OrderItem(
                                uuid = "",
                                status = OrderStatus.NOT_ACCEPTED,
                                code = "А-01",
                                dateTime = "18.03.2023 11:53",
                                statusName = OrderStatus.NOT_ACCEPTED.name,
                                background = OrderStatus.NOT_ACCEPTED.getOrderColor(),
                                isLast = false,
                            ),
                            OrderItem(
                                uuid = "",
                                status = OrderStatus.ACCEPTED,
                                code = "Б-02",
                                dateTime = "18.03.2023 11:53",
                                statusName = OrderStatus.ACCEPTED.name,
                                background = OrderStatus.ACCEPTED.getOrderColor(),
                                isLast = false,
                            ),
                            OrderItem(
                                uuid = "",
                                status = OrderStatus.PREPARING,
                                code = "В-03",
                                dateTime = "18.03.2023 11:53",
                                statusName = OrderStatus.PREPARING.name,
                                background = OrderStatus.PREPARING.getOrderColor(),
                                isLast = false,
                            ),
                            OrderItem(
                                uuid = "",
                                status = OrderStatus.DONE,
                                code = "Г-04",
                                dateTime = "18.03.2023 11:53",
                                statusName = OrderStatus.DONE.name,
                                background = OrderStatus.DONE.getOrderColor(),
                                isLast = false,
                            ),
                            OrderItem(
                                uuid = "",
                                status = OrderStatus.SENT_OUT,
                                code = "Д-05",
                                dateTime = "18.03.2023 11:53",
                                statusName = OrderStatus.SENT_OUT.name,
                                background = OrderStatus.SENT_OUT.getOrderColor(),
                                isLast = false,
                            ),
                            OrderItem(
                                uuid = "",
                                status = OrderStatus.DELIVERED,
                                code = "Е-06",
                                dateTime = "18.03.2023 11:53",
                                statusName = OrderStatus.DELIVERED.name,
                                background = OrderStatus.DELIVERED.getOrderColor(),
                                isLast = false,
                            ),
                            OrderItem(
                                uuid = "",
                                status = OrderStatus.CANCELED,
                                code = "Ж-07",
                                dateTime = "18.03.2023 11:53",
                                statusName = OrderStatus.CANCELED.name,
                                background = OrderStatus.CANCELED.getOrderColor(),
                                isLast = true,
                            ),
                        ),
                ),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OrderListScreenErrorPreview() {
    FoodDeliveryTheme {
        OrderListScreen(
            onAction = { },
            viewState =
                orderListViewStateMock.copy(
                    state = OrderListViewState.State.Error,
                ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OrderListScreenLoadingPreview() {
    FoodDeliveryTheme {
        OrderListScreen(
            onAction = { },
            viewState =
                orderListViewStateMock.copy(
                    state = OrderListViewState.State.Loading,
                ),
        )
    }
}
