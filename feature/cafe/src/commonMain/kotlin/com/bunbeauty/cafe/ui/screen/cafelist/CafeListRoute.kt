package com.bunbeauty.cafe.ui.screen.cafelist

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bunbeauty.cafe.presentation.cafe_list.CafeList
import com.bunbeauty.cafe.presentation.cafe_list.CafeListViewModel
import com.bunbeauty.cafe.ui.CafeItem
import com.bunbeauty.cafe.ui.CafeItemAndroid
import com.bunbeauty.cafe.ui.model.CafeOptions
import com.bunbeauty.cafe.ui.screen.cafeoptions.CafeOptionsBottomSheet
import com.bunbeauty.core.model.cafe.CafeOpenState
import com.bunbeauty.designsystem.theme.FoodDeliveryTheme
import com.bunbeauty.designsystem.ui.element.FoodDeliveryScaffold
import com.bunbeauty.designsystem.ui.element.card.FoodDeliveryItem
import com.bunbeauty.designsystem.ui.element.footer.WebStickyFooterLazyColumn
import com.bunbeauty.designsystem.ui.screen.ErrorScreen
import com.bunbeauty.designsystem.ui.screen.LoadingScreen
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import papakarlo.designsystem.generated.resources.Res
import papakarlo.designsystem.generated.resources.error_cafe_list_loading
import papakarlo.designsystem.generated.resources.title_cafe_list

@Composable
fun CafeList.DataState.mapState(): CafeListViewState = toViewState()

@Composable
fun CafeListRoute(
    viewModel: CafeListViewModel = koinViewModel(),
    back: () -> Unit,
) {
    LaunchedEffect(Unit) {
        viewModel.onAction(CafeList.Action.Init)
    }

    val viewState by viewModel.dataState.collectAsStateWithLifecycle()

    val onAction =
        remember {
            { action: CafeList.Action ->
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

    CafeListEffect(
        effects = effects,
        back = back,
        consumeEffects = consumeEffects,
    )
    CafeListScreen(viewState = viewState.mapState(), onAction = onAction)
}

@Composable
private fun CafeListScreen(
    viewState: CafeListViewState,
    onAction: (CafeList.Action) -> Unit,
) {
    FoodDeliveryScaffold(
        title = stringResource(Res.string.title_cafe_list),
        backActionClick = {
            onAction(CafeList.Action.BackClicked)
        },
        backgroundColor = FoodDeliveryTheme.colors.mainColors.surface,
    ) {
        when (val state = viewState) {
            is CafeListViewState.Error ->
                ErrorScreen(
                    mainTextId = Res.string.error_cafe_list_loading,
                    onClick = {
                        onAction(CafeList.Action.OnRefreshClicked)
                    },
                )

            CafeListViewState.Loading -> LoadingScreen()

            is CafeListViewState.Success -> {
                CafeListSuccessScreen(
                    viewState.cafeList,
                    onAction = onAction,
                )
                CafeOptionsBottomSheet(
                    cafeOptionUI = state.cafeOptionUI,
                    onAction = onAction,
                )
            }
        }
    }
}

@Composable
private fun CafeListSuccessScreen(
    cafeItemList: List<CafeItemAndroid>,
    onAction: (CafeList.Action) -> Unit,
) {
    WebStickyFooterLazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        itemsIndexed(cafeItemList) { _, cafeItem ->
            FoodDeliveryItem(needDivider = !cafeItem.isLast) {
                CafeItem(
                    cafeItem = cafeItem,
                    onClick = {
                        onAction(CafeList.Action.OnCafeClicked(cafeUuid = cafeItem.uuid))
                    },
                )
            }
        }
    }
}

@Composable
fun CafeListEffect(
    effects: List<CafeList.Event>,
    back: () -> Unit,
    consumeEffects: () -> Unit,
) {
    LaunchedEffect(effects) {
        effects.forEach { effect ->
            when (effect) {
                CafeList.Event.Back -> back()
            }
        }
        consumeEffects()
    }
}

@Preview(showBackground = true)
@Composable
private fun CafeListSuccessScreenPreview() {
    FoodDeliveryTheme {
        CafeListScreen(
            viewState =
                CafeListViewState.Success(
                    cafeList =
                        persistentListOf(
                            CafeItemAndroid(
                                uuid = "",
                                address = "улица Чапаева, д. 22аб кв. 55, 1 подъезд, 1 этаж",
                                workingHours = "9:00 - 22:00",
                                cafeStatusText = "Open",
                                phone = "00000000",
                                cafeOpenState = CafeOpenState.Opened,
                                isLast = false,
                            ),
                            CafeItemAndroid(
                                uuid = "",
                                address = "улица Чапаева, д. 22аб кв. 55, 1 подъезд, 1 этаж",
                                workingHours = "9:00 - 22:00",
                                cafeStatusText = "Close soon",
                                phone = "00000000",
                                cafeOpenState = CafeOpenState.CloseSoon(30),
                                isLast = false,
                            ),
                            CafeItemAndroid(
                                uuid = "",
                                address = "улица Чапаева, д. 22аб кв. 55, 1 подъезд, 1 этаж",
                                workingHours = "9:00 - 22:00",
                                cafeStatusText = "Closed",
                                phone = "00000000",
                                cafeOpenState = CafeOpenState.Closed,
                                isLast = true,
                            ),
                        ),
                    cafeOptionUI =
                        CafeListViewState.CafeOptionUI(
                            isShown = false,
                            cafeOptions = null,
                        ),
                ),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CafeListSuccessWithBottomSheetScreenPreview() {
    FoodDeliveryTheme {
        CafeListScreen(
            viewState =
                CafeListViewState.Success(
                    cafeList =
                        persistentListOf(
                            CafeItemAndroid(
                                uuid = "",
                                address = "улица Чапаева, д. 22аб кв. 55, 1 подъезд, 1 этаж",
                                workingHours = "9:00 - 22:00",
                                cafeStatusText = "Open",
                                phone = "00000000",
                                cafeOpenState = CafeOpenState.Opened,
                                isLast = false,
                            ),
                            CafeItemAndroid(
                                uuid = "",
                                address = "улица Чапаева, д. 22аб кв. 55, 1 подъезд, 1 этаж",
                                workingHours = "9:00 - 22:00",
                                cafeStatusText = "Close soon",
                                phone = "00000000",
                                cafeOpenState = CafeOpenState.CloseSoon(30),
                                isLast = false,
                            ),
                            CafeItemAndroid(
                                uuid = "",
                                address = "улица Чапаева, д. 22аб кв. 55, 1 подъезд, 1 этаж",
                                workingHours = "9:00 - 22:00",
                                cafeStatusText = "Closed",
                                phone = "00000000",
                                cafeOpenState = CafeOpenState.Closed,
                                isLast = true,
                            ),
                        ),
                    cafeOptionUI =
                        CafeListViewState.CafeOptionUI(
                            isShown = true,
                            cafeOptions =
                                CafeOptions(
                                    title = "улица Чапаева, д 22А",
                                    showOnMap = "На карте: улица Чапаева, д 22А",
                                    callToCafe = "Позвонить: +7 (900) 900-90-90",
                                    phone = "",
                                    latitude = 0.0,
                                    longitude = 0.0,
                                ),
                        ),
                ),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CafeListLoadingScreenPreview() {
    FoodDeliveryTheme {
        CafeListScreen(
            viewState = CafeListViewState.Loading,
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CafeListErrorScreenPreview() {
    FoodDeliveryTheme {
        CafeListScreen(
            viewState = CafeListViewState.Error(Throwable()),
            onAction = {},
        )
    }
}
