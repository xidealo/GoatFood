package com.bunbeauty.order.ui.screen.orderdetails

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bunbeauty.core.model.order.OrderStatus
import com.bunbeauty.designsystem.theme.FoodDeliveryTheme
import com.bunbeauty.designsystem.theme.bold
import com.bunbeauty.designsystem.theme.medium
import com.bunbeauty.designsystem.ui.LocalBottomBarPadding
import com.bunbeauty.designsystem.ui.element.FoodDeliveryScaffold
import com.bunbeauty.designsystem.ui.element.card.DiscountCard
import com.bunbeauty.designsystem.ui.element.card.FoodDeliveryCard
import com.bunbeauty.designsystem.ui.element.card.FoodDeliveryItem
import com.bunbeauty.designsystem.ui.element.footer.WebStickyFooterLazyColumn
import com.bunbeauty.designsystem.ui.element.surface.FoodDeliverySurface
import com.bunbeauty.designsystem.ui.screen.ErrorScreen
import com.bunbeauty.designsystem.ui.screen.LoadingScreen
import com.bunbeauty.order.presentation.order_details.OrderDetails
import com.bunbeauty.order.presentation.order_details.OrderDetailsViewModel
import com.bunbeauty.order.ui.ui.OrderProductItem
import com.bunbeauty.order.ui.ui.OrderStatusBar
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import papakarlo.designsystem.generated.resources.Res
import papakarlo.designsystem.generated.resources.error_order_details_discount
import papakarlo.designsystem.generated.resources.msg_order_details_address
import papakarlo.designsystem.generated.resources.msg_order_details_comment
import papakarlo.designsystem.generated.resources.msg_order_details_date_time
import papakarlo.designsystem.generated.resources.msg_order_details_delivery_cost
import papakarlo.designsystem.generated.resources.msg_order_details_discount
import papakarlo.designsystem.generated.resources.msg_order_details_order_cost
import papakarlo.designsystem.generated.resources.msg_order_details_payment_method
import papakarlo.designsystem.generated.resources.msg_order_details_pickup_method

@Composable
fun OrderDetailsRoute(
    viewModel: OrderDetailsViewModel = koinViewModel(),
    orderUuid: String,
    back: () -> Unit,
) {
    LifecycleStartEffect(Unit) {
        viewModel.onAction(OrderDetails.Action.StartObserve(orderUuid = orderUuid))
        onStopOrDispose {
            viewModel.onAction(OrderDetails.Action.StopObserve)
        }
    }

    val viewState by viewModel.dataState.collectAsStateWithLifecycle()

    val onAction =
        remember {
            { action: OrderDetails.Action ->
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

    OrderDetailsEffect(
        effects = effects,
        back = back,
        consumeEffects = consumeEffects,
    )

    OrderDetailsScreen(
        viewState = viewState.mapToOrderDetailsViewState(),
        onAction = onAction,
    )
}

@Composable
fun OrderDetailsEffect(
    effects: List<OrderDetails.Event>,
    back: () -> Unit,
    consumeEffects: () -> Unit,
) {
    LaunchedEffect(effects) {
        effects.forEach { effect ->
            when (effect) {
                OrderDetails.Event.Back -> back()
            }
        }
        consumeEffects()
    }
}

@Composable
private fun OrderDetailsScreen(
    viewState: OrderDetailsViewState,
    onAction: (OrderDetails.Action) -> Unit,
) {
    FoodDeliveryScaffold(
        title = viewState.code,
        backActionClick = {
            onAction(OrderDetails.Action.Back)
        },
        backgroundColor = FoodDeliveryTheme.colors.mainColors.surface,
    ) {
        Crossfade(
            targetState = viewState.state,
            label = "ConsumerCart",
        ) { screenState ->
            when (screenState) {
                OrderDetails.DataState.ScreenState.LOADING -> LoadingScreen()
                OrderDetails.DataState.ScreenState.SUCCESS ->
                    OrderDetailsSuccessScreen(
                        viewState,
                    )

                OrderDetails.DataState.ScreenState.ERROR ->
                    ErrorScreen(Res.string.error_order_details_discount) {
                        onAction(OrderDetails.Action.Reload(viewState.orderUuid))
                    }
            }
        }
    }
}

@Composable
private fun OrderDetailsSuccessScreen(state: OrderDetailsViewState) {
    Column(modifier = Modifier.fillMaxSize()) {
        WebStickyFooterLazyColumn(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
            contentPadding =
                PaddingValues(
                    bottom =
                        FoodDeliveryTheme.dimensions.screenContentSpace +
                            LocalBottomBarPadding.current,
                ),
            verticalArrangement = spacedBy(8.dp),
        ) {
            item(key = "OrderStatusBar") {
                state.orderInfo?.let { orderInfo ->
                    OrderStatusBar(
                        orderStatus = orderInfo.status,
                        orderStatusName = orderInfo.statusName,
                    )
                }
            }

            item(key = "OrderInfoCard") {
                state.orderInfo?.let { orderInfo ->
                    OrderInfoCard(
                        modifier =
                            Modifier
                                .fillMaxWidth(),
                        orderInfo = orderInfo,
                    )
                }
            }

            items(
                items = state.orderProductItemList,
                key = { orderProductItem ->
                    orderProductItem.key
                },
            ) { orderProductItem ->
                FoodDeliveryItem(needDivider = !orderProductItem.isLast) {
                    OrderProductItem(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        orderProductItem = orderProductItem,
                    )
                }
            }
        }
        BottomAmountBar(state)
    }
}

@Composable
private fun OrderInfoTextColumn(
    modifier: Modifier = Modifier,
    hint: String,
    info: String,
) {
    Column(modifier = modifier) {
        Text(
            text = hint,
            style = FoodDeliveryTheme.typography.labelSmall.medium,
            color = FoodDeliveryTheme.colors.mainColors.onSurfaceVariant,
        )
        Text(
            text = info,
            style = FoodDeliveryTheme.typography.bodyMedium,
            color = FoodDeliveryTheme.colors.mainColors.onSurface,
        )
    }
}

@Composable
private fun OrderInfoCard(
    modifier: Modifier = Modifier,
    orderInfo: OrderDetailsViewState.OrderInfo,
) {
    FoodDeliveryCard(
        modifier = modifier,
        clickable = false,
        elevated = false,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                OrderInfoTextColumn(
                    modifier =
                        Modifier
                            .weight(1f),
                    hint = stringResource(Res.string.msg_order_details_date_time),
                    info = orderInfo.dateTime,
                )
                orderInfo.deferredTime?.let { deferredTime ->
                    OrderInfoTextColumn(
                        modifier =
                            Modifier
                                .padding(start = 16.dp)
                                .weight(1f),
                        hint = orderInfo.deferredTimeHint,
                        info = deferredTime,
                    )
                }
            }
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
            ) {
                OrderInfoTextColumn(
                    modifier =
                        Modifier
                            .weight(1f),
                    hint = stringResource(Res.string.msg_order_details_pickup_method),
                    info = orderInfo.pickupMethod,
                )
                orderInfo.paymentMethod?.let { paymentMethod ->
                    OrderInfoTextColumn(
                        modifier =
                            Modifier
                                .padding(start = 16.dp)
                                .weight(1f),
                        hint = stringResource(Res.string.msg_order_details_payment_method),
                        info = paymentMethod,
                    )
                }
            }
            OrderInfoTextColumn(
                modifier =
                    Modifier
                        .padding(top = 8.dp),
                hint = stringResource(Res.string.msg_order_details_address),
                info = orderInfo.address,
            )
            orderInfo.comment?.let { comment ->
                OrderInfoTextColumn(
                    modifier =
                        Modifier
                            .padding(top = 8.dp),
                    hint = stringResource(Res.string.msg_order_details_comment),
                    info = comment,
                )
            }
        }
    }
}

@Composable
private fun BottomAmountBar(orderDetailsViewState: OrderDetailsViewState) {
    FoodDeliverySurface(
        modifier = Modifier.fillMaxWidth(),
        elevated = false,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(FoodDeliveryTheme.colors.mainColors.surface)
                    .padding(FoodDeliveryTheme.dimensions.mediumSpace)
                    .padding(bottom = LocalBottomBarPadding.current),
        ) {
            orderDetailsViewState.discount?.let { discount ->
                Row(modifier = Modifier.padding(bottom = 8.dp)) {
                    Text(
                        text = stringResource(Res.string.msg_order_details_discount),
                        style = FoodDeliveryTheme.typography.bodyMedium,
                        color = FoodDeliveryTheme.colors.mainColors.onSurface,
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    DiscountCard(discount = discount)
                }
            }

            orderDetailsViewState.deliveryCost?.let { deliveryCost ->
                Row(modifier = Modifier.padding(bottom = 8.dp)) {
                    Text(
                        text = stringResource(Res.string.msg_order_details_delivery_cost),
                        style = FoodDeliveryTheme.typography.bodyMedium,
                        color = FoodDeliveryTheme.colors.mainColors.onSurface,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        modifier = Modifier.weight(1f),
                        text = deliveryCost,
                        style = FoodDeliveryTheme.typography.bodyMedium,
                        color = FoodDeliveryTheme.colors.mainColors.onSurface,
                        textAlign = TextAlign.End,
                    )
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(Res.string.msg_order_details_order_cost),
                    style = FoodDeliveryTheme.typography.bodyMedium.bold,
                    color = FoodDeliveryTheme.colors.mainColors.onSurface,
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = orderDetailsViewState.newTotalCost,
                    style = FoodDeliveryTheme.typography.bodyMedium.bold,
                    color = FoodDeliveryTheme.colors.mainColors.onSurface,
                    textAlign = TextAlign.End,
                )
            }
        }
    }
}

@Preview
@Composable
private fun OrderInfoCardPreview() {
    FoodDeliveryTheme {
        OrderInfoCard(orderInfo = getOrderInfo())
    }
}

@Preview
@Composable
private fun OrderInfoCardWithoutDeferredTimeAndCommentPreview() {
    FoodDeliveryTheme {
        OrderInfoCard(
            orderInfo =
                getOrderInfo().copy(
                    deferredTime = null,
                    comment = null,
                ),
        )
    }
}

@Preview
@Composable
private fun BottomAmountBarPreview() {
    FoodDeliveryTheme {
        BottomAmountBar(orderDetailsViewState = getOrderDetails())
    }
}

@Preview
@Composable
private fun BottomAmountBarWithoutDeliveryPreview() {
    FoodDeliveryTheme {
        BottomAmountBar(
            orderDetailsViewState = getOrderDetails().copy(deliveryCost = null),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OrderDetailsSuccessScreenPreview() {
    FoodDeliveryTheme {
        OrderDetailsScreen(
            viewState = getOrderDetails(),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OrderDetailsLoadingScreenPreview() {
    FoodDeliveryTheme {
        OrderDetailsScreen(
            viewState =
                getOrderDetails().copy(
                    state = OrderDetails.DataState.ScreenState.LOADING,
                ),
            onAction = {
            },
        )
    }
}

private fun getOrderDetails(): OrderDetailsViewState =
    OrderDetailsViewState(
        orderProductItemList =
            listOf(
                OrderProductUiItem(
                    uuid = "",
                    name = "Product 1",
                    newPrice = "100 ₽",
                    newCost = "200 ₽",
                    photoLink = "",
                    count = "× 2",
                    key = "k1",
                    additions = null,
                    isLast = false,
                ),
                OrderProductUiItem(
                    uuid = "",
                    name = "Product 2",
                    newPrice = "150 ₽",
                    newCost = "150 ₽",
                    photoLink = "",
                    count = "× 1",
                    key = "k2",
                    additions = "Необычный лаваш • Добавка 1 • Добавка 2",
                    isLast = true,
                ),
            ),
        orderInfo = getOrderInfo(),
        deliveryCost = "100 ₽",
        newTotalCost = "550 ₽",
        state = OrderDetails.DataState.ScreenState.SUCCESS,
        code = "A-40",
        discount = "10%",
        orderUuid = "sdas",
    )

private fun getOrderInfo(): OrderDetailsViewState.OrderInfo =
    OrderDetailsViewState.OrderInfo(
        status = OrderStatus.PREPARING,
        dateTime = "19.03.2023",
        deferredTime = "10:30",
        address =
            "" +
                "ул. Лука" +
                "2" +
                "10" +
                "1" +
                "3" +
                "тест",
        comment = "давай кушать",
        pickupMethod = "доставка",
        statusName = "Готовится",
        deferredTimeHint = "Время самовывоза",
        paymentMethod = "Наличными",
    )
