package com.bunbeauty.menu.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bunbeauty.core.Constants.FAB_SNACKBAR_BOTTOM_PADDING
import com.bunbeauty.core.domain.review.AppReviewManager
import com.bunbeauty.core.domain.review.AppReviewSource
import com.bunbeauty.core.model.ProductDetailsOpenedFrom
import com.bunbeauty.menu.presentation.MenuState
import com.bunbeauty.menu.presentation.MenuViewModel
import com.bunbeauty.menu.ui.mapper.mapState
import org.jetbrains.compose.resources.getString
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import papakarlo.designsystem.generated.resources.Res
import papakarlo.designsystem.generated.resources.error_consumer_cart_add_product
import papakarlo.designsystem.generated.resources.msg_menu_product_added

@Composable
fun MenuRoute(
    animatedContentScope: AnimatedContentScope,
    viewModel: MenuViewModel = koinViewModel(),
    savedStateHandle: SavedStateHandle,
    scrollToTopKey: String,
    goToProductDetailsFragment: (
        uuid: String,
        name: String,
        productDetailsOpenedFrom: ProductDetailsOpenedFrom,
    ) -> Unit,
    goToProfile: () -> Unit,
    goToConsumerCart: () -> Unit,
    goToOrderDetailsFragment: (String) -> Unit,
    showErrorMessage: (String) -> Unit,
    showInfoMessage: (String, Int) -> Unit,
) {
    val dataState by viewModel.dataState.collectAsStateWithLifecycle()
    val hapticFeedback = rememberCategoryHapticFeedback()

    val onAction =
        remember(viewModel, hapticFeedback) {
            { action: MenuState.Action ->
                if (action is MenuState.Action.OnCategoryClicked) {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
                }
                viewModel.onAction(action)
            }
        }

    val effects by viewModel.events.collectAsStateWithLifecycle()
    val consumeEffects =
        remember(viewModel) {
            {
                viewModel.consumeEvents(effects)
            }
        }

    LifecycleStartEffect(Unit) {
        onAction(MenuState.Action.StartLastOrderObservation)
        onAction(MenuState.Action.RefreshDiscountAndFavorites)
        onStopOrDispose {
            onAction(MenuState.Action.StopLastOrderObservation)
        }
    }

    LaunchedEffect(savedStateHandle, scrollToTopKey) {
        savedStateHandle.getStateFlow(scrollToTopKey, false).collect { shouldScrollToTop ->
            if (shouldScrollToTop) {
                savedStateHandle[scrollToTopKey] = false
                onAction(MenuState.Action.ScrollToTop)
            }
        }
    }

    val appReviewManager = koinInject<AppReviewManager>()

    MenuEffect(
        effects = effects,
        goToProductDetailsFragment = goToProductDetailsFragment,
        goToProfile = goToProfile,
        goToConsumerCart = goToConsumerCart,
        goToOrderDetailsFragment = goToOrderDetailsFragment,
        consumeEffects = consumeEffects,
        showErrorMessage = showErrorMessage,
        showInfoMessage = showInfoMessage,
        appReviewManager = appReviewManager,
    )

    MenuScreen(
        viewState = dataState.mapState(),
        onAction = onAction,
        animatedContentScope = animatedContentScope,
    )
}

@Composable
internal fun MenuEffect(
    effects: List<MenuState.Event>,
    goToProductDetailsFragment: (
        uuid: String,
        name: String,
        productDetailsOpenedFrom: ProductDetailsOpenedFrom,
    ) -> Unit,
    goToProfile: () -> Unit,
    goToConsumerCart: () -> Unit,
    goToOrderDetailsFragment: (String) -> Unit,
    consumeEffects: () -> Unit,
    showErrorMessage: (String) -> Unit,
    showInfoMessage: (String, Int) -> Unit,
    appReviewManager: AppReviewManager,
) {
    LaunchedEffect(effects) {
        effects.forEach { effect ->
            when (effect) {
                is MenuState.Event.GoToSelectedItem -> {
                    goToProductDetailsFragment(
                        effect.uuid,
                        effect.name,
                        ProductDetailsOpenedFrom.MENU_PRODUCT,
                    )
                }

                MenuState.Event.ShowAddProductError -> {
                    showErrorMessage(getString(Res.string.error_consumer_cart_add_product))
                }

                is MenuState.Event.ShowAddedProduct -> {
                    showInfoMessage(
                        getString(Res.string.msg_menu_product_added),
                        FAB_SNACKBAR_BOTTOM_PADDING,
                    )
                }

                is MenuState.Event.OpenOrderDetails -> {
                    goToOrderDetailsFragment(effect.uuid)
                }

                MenuState.Event.OpenProfile -> {
                    goToProfile()
                }

                MenuState.Event.OpenConsumerCart -> {
                    goToConsumerCart()
                }

                is MenuState.Event.RequestAppReview ->
                    appReviewManager.request(
                        orderUuid = effect.orderUuid,
                        source = AppReviewSource.MENU,
                    )
            }
        }
        consumeEffects()
    }
}
