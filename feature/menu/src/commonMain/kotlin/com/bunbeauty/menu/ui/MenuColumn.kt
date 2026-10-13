package com.bunbeauty.menu.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bunbeauty.core.model.DiscountSource
import com.bunbeauty.designsystem.theme.FoodDeliveryTheme
import com.bunbeauty.designsystem.theme.bold
import com.bunbeauty.designsystem.theme.logoMedium
import com.bunbeauty.designsystem.theme.medium
import com.bunbeauty.designsystem.ui.LocalBottomBarPadding
import com.bunbeauty.designsystem.ui.LocalStatusBarColor
import com.bunbeauty.designsystem.ui.element.FoodDeliveryProductItem
import com.bunbeauty.designsystem.ui.element.card.BannerCard
import com.bunbeauty.designsystem.ui.element.footer.webSiteFooterItem
import com.bunbeauty.designsystem.ui.icon24
import com.bunbeauty.designsystem.ui.ignoreHorizontalParentPadding
import com.bunbeauty.menu.presentation.MenuState
import com.bunbeauty.menu.ui.state.MenuItemUi
import com.bunbeauty.menu.ui.state.MenuViewState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import papakarlo.designsystem.generated.resources.Res
import papakarlo.designsystem.generated.resources.description_ic_discount
import papakarlo.designsystem.generated.resources.description_login_logo
import papakarlo.designsystem.generated.resources.ic_discount
import papakarlo.designsystem.generated.resources.ic_profile
import papakarlo.designsystem.generated.resources.msg_menu_discount
import papakarlo.designsystem.generated.resources.msg_menu_personal_discount
import papakarlo.designsystem.generated.resources.title_menu
import papakarlo.designsystem.generated.resources.title_menu_discount

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun MenuColumn(
    menu: MenuViewState,
    menuLazyListState: LazyGridState,
    animatedContentScope: AnimatedVisibilityScope,
    onAction: (MenuState.Action) -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        // Keep exactly 2 columns on phones (< 600.dp) so the mobile layout is
        // unchanged, and add more columns as the viewport gets wider (tablets/web).
        val columnCount =
            when {
                maxWidth < 600.dp -> 2
                maxWidth < 840.dp -> 3
                maxWidth < 1080.dp -> 4
                else -> 5
            }
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            contentPadding =
                PaddingValues(
                    bottom = 96.dp + LocalBottomBarPadding.current,
                    start = 16.dp,
                    end = 16.dp,
                ),
            columns = GridCells.Fixed(columnCount),
            horizontalArrangement = spacedBy(8.dp),
            state = menuLazyListState,
            userScrollEnabled = menu.userScrollEnabled,
        ) {
            item(
                key = "TopBar",
                span = {
                    GridItemSpan(maxLineSpan)
                },
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .ignoreHorizontalParentPadding(horizontal = 16.dp)
                            .background(LocalStatusBarColor.current.value),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .background(
                                    color = FoodDeliveryTheme.colors.mainColors.primary,
                                    shape =
                                        RoundedCornerShape(
                                            bottomEnd = 32.dp,
                                            bottomStart = 32.dp,
                                        ),
                                ).statusBarsPadding(),
                    ) {
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = 16.dp,
                                        end = 8.dp,
                                        top = 8.dp,
                                    ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = stringResource(Res.string.title_menu),
                                maxLines = 1,
                                style = FoodDeliveryTheme.typography.titleLarge.medium,
                                overflow = TextOverflow.Ellipsis,
                                color = FoodDeliveryTheme.colors.mainColors.onPrimary,
                            )

                            IconButton(
                                onClick = { onAction(MenuState.Action.OnProfileClicked) },
                            ) {
                                Icon(
                                    modifier = Modifier.icon24(),
                                    painter = painterResource(resource = Res.drawable.ic_profile),
                                    tint = FoodDeliveryTheme.colors.mainColors.onPrimary,
                                    contentDescription = null,
                                )
                            }
                        }

                        logoMedium?.let { logo ->
                            Image(
                                modifier =
                                    Modifier
                                        .height(height = 96.dp)
                                        .padding(vertical = 16.dp)
                                        .align(Alignment.Center),
                                painter = painterResource(resource = logo),
                                contentDescription = stringResource(resource = Res.string.description_login_logo),
                            )
                        }
                    }
                }
            }

            stickyHeader {
                MenuCategoryRow(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .ignoreHorizontalParentPadding(horizontal = 16.dp),
                    categoryItemList = menu.categoryItemList,
                    menuItemList = menu.menuItemList,
                    menuLazyGridState = menuLazyListState,
                    onAction = onAction,
                )
            }

            item(
                key = "LastOrder",
                span = {
                    GridItemSpan(maxLineSpan)
                },
            ) {
                AnimatedVisibility(
                    visible = menu.lastOrder != null,
                    enter =
                        fadeIn(animationSpec = tween(durationMillis = 300)) +
                            expandVertically(animationSpec = tween(durationMillis = 300)),
                    exit =
                        fadeOut(animationSpec = tween(durationMillis = 300)) +
                            shrinkVertically(animationSpec = tween(durationMillis = 300)),
                ) {
                    menu.lastOrder?.let { lastOrder ->
                        LastOrderMenuItem(
                            onClick = {
                                onAction(MenuState.Action.OnLastOrderClicked(lastOrder.uuid))
                            },
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .ignoreHorizontalParentPadding(horizontal = 16.dp),
                            lastOrder = lastOrder,
                        )
                    }
                }
            }

            itemsIndexed(
                items = menu.menuItemList,
                key = { _, menuItemModel -> menuItemModel.key },
                span = { _, menuItemModel ->
                    when (menuItemModel) {
                        is MenuItemUi.Discount,
                        is MenuItemUi.Favorites,
                        is MenuItemUi.CategoryHeader,
                        -> GridItemSpan(maxLineSpan)

                        else -> GridItemSpan(1)
                    }
                },
            ) { _, menuItem ->
                when (menuItem) {
                    is MenuItemUi.Discount -> {
                        val discountMessage =
                            if (menuItem.source == DiscountSource.PERSONAL) {
                                stringResource(
                                    resource = Res.string.msg_menu_personal_discount,
                                    menuItem.discount,
                                )
                            } else {
                                stringResource(
                                    resource = Res.string.msg_menu_discount,
                                    menuItem.discount,
                                )
                            }
                        BannerCard(
                            title =
                                stringResource(
                                    resource = Res.string.title_menu_discount,
                                    menuItem.discount,
                                ),
                            text = discountMessage,
                            icon = Res.drawable.ic_discount,
                            iconDescription =
                                stringResource(
                                    Res.string.description_ic_discount,
                                ),
                            modifier =
                                Modifier
                                    .padding(top = 8.dp),
                        )
                    }

                    is MenuItemUi.Favorites -> {
                        MenuFavoritesRow(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .ignoreHorizontalParentPadding(horizontal = 16.dp),
                            products = menuItem.products,
                            animatedContentScope = animatedContentScope,
                            onAction = onAction,
                        )
                    }

                    is MenuItemUi.CategoryHeader -> {
                        Text(
                            modifier =
                                Modifier.padding(
                                    top = 16.dp,
                                ),
                            text = menuItem.name,
                            style = FoodDeliveryTheme.typography.titleMedium.bold,
                            color = FoodDeliveryTheme.colors.mainColors.onSurface,
                        )
                    }

                    is MenuItemUi.Product -> {
                        FoodDeliveryProductItem(
                            animatedContentScope = animatedContentScope,
                            modifier =
                                Modifier
                                    .padding(
                                        top = 12.dp,
                                    ),
                            onAddProductClick = { uuid ->
                                onAction(MenuState.Action.OnAddProductClicked(uuid))
                            },
                            onProductClick = { uuid ->
                                onAction(MenuState.Action.OnMenuItemClicked(uuid))
                            },
                            uuid = menuItem.product.uuid,
                            photoLink = menuItem.product.photoLink,
                            name = menuItem.product.name,
                            oldPrice = menuItem.product.oldPrice,
                            newPrice = menuItem.product.newPrice,
                        )
                    }
                }
            }

            webSiteFooterItem(
                modifier =
                    Modifier
                        .padding(top = 16.dp)
                        .ignoreHorizontalParentPadding(horizontal = 16.dp),
            )
        }
    }
}
