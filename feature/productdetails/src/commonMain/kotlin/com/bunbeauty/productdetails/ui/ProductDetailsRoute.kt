package com.bunbeauty.productdetails.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bunbeauty.core.Constants.FAB_SNACKBAR_BOTTOM_PADDING
import com.bunbeauty.core.model.ProductDetailsOpenedFrom
import com.bunbeauty.designsystem.theme.FoodDeliveryTheme
import com.bunbeauty.designsystem.theme.bold
import com.bunbeauty.designsystem.ui.LocalBottomBarPadding
import com.bunbeauty.designsystem.ui.SharedTransitionScopeComposition
import com.bunbeauty.designsystem.ui.element.FoodDeliveryAsyncImage
import com.bunbeauty.designsystem.ui.element.FoodDeliveryAction
import com.bunbeauty.designsystem.ui.element.FoodDeliveryScaffold
import com.bunbeauty.designsystem.ui.element.TopCartUi
import com.bunbeauty.designsystem.ui.element.addition.AdditionCardItem
import com.bunbeauty.designsystem.ui.element.addition.AdditionRowItem
import com.bunbeauty.designsystem.ui.element.button.FoodDeliveryExtendedFab
import com.bunbeauty.designsystem.ui.element.card.FoodDeliveryItem
import com.bunbeauty.designsystem.ui.element.footer.webSiteFooterItem
import com.bunbeauty.designsystem.ui.screen.ErrorScreen
import com.bunbeauty.designsystem.ui.screen.LoadingScreen
import com.bunbeauty.productdetails.presentation.AdditionItem
import com.bunbeauty.productdetails.presentation.MenuProductAdditionItem
import com.bunbeauty.productdetails.presentation.ProductDetailsState
import com.bunbeauty.productdetails.presentation.ProductDetailsViewModel
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import papakarlo.designsystem.generated.resources.Res
import papakarlo.designsystem.generated.resources.action_product_details_want
import papakarlo.designsystem.generated.resources.common_error
import papakarlo.designsystem.generated.resources.description_product
import papakarlo.designsystem.generated.resources.error_consumer_cart_add_product
import papakarlo.designsystem.generated.resources.description_favorite
import papakarlo.designsystem.generated.resources.error_something_went_wrong
import papakarlo.designsystem.generated.resources.ic_favorite_24
import papakarlo.designsystem.generated.resources.ic_favorite_filled_24
import papakarlo.designsystem.generated.resources.ic_plus_16
import papakarlo.designsystem.generated.resources.msg_menu_product_added
import papakarlo.designsystem.generated.resources.msg_menu_product_edited

// Caps the content width on wide (web/tablet) screens so the product photo and
// text don't stretch edge to edge. On phones the screen is narrower than this, so
// the layout stays exactly the same as before.
private val ProductDetailsMaxWidth = 640.dp

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ProductDetailsRoute(
    animatedContentScope: AnimatedContentScope,
    viewModel: ProductDetailsViewModel = koinViewModel(),
    menuProductUuid: String,
    menuProductName: String,
    productDetailsOpenedFrom: ProductDetailsOpenedFrom,
    additionUuidList: List<String>,
    cartProductUuid: String?,
    back: () -> Unit,
    showInfoMessage: (String, Int) -> Unit,
    showErrorMessage: (String) -> Unit,
) {
    LaunchedEffect(Unit) {
        viewModel.onAction(
            ProductDetailsState.Action.Init(
                menuProductUuid = menuProductUuid,
                selectedAdditionUuidList = additionUuidList,
            ),
        )
    }

    val viewState by viewModel.dataState.collectAsStateWithLifecycle()

    val onAction =
        remember {
            { action: ProductDetailsState.Action ->
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

    ProductDetailsEffect(
        effects = effects,
        back = back,
        consumeEffects = consumeEffects,
        showInfoMessage = showInfoMessage,
        showErrorMessage = showErrorMessage,
    )

    ProductDetailsScreen(
        menuProductName = menuProductName,
        menuProductUuid = menuProductUuid,
        additionUuidList = additionUuidList,
        productDetailsViewState = viewState.map(),
        onAction = onAction,
        productDetailsOpenedFrom = productDetailsOpenedFrom,
        cartProductUuid = cartProductUuid,
        animatedContentScope = animatedContentScope,
    )
}

@Composable
fun ProductDetailsEffect(
    effects: List<ProductDetailsState.Event>,
    back: () -> Unit,
    consumeEffects: () -> Unit,
    showInfoMessage: (String, Int) -> Unit,
    showErrorMessage: (String) -> Unit,
) {
    LaunchedEffect(effects) {
        effects.forEach { effect ->
            when (effect) {
                ProductDetailsState.Event.NavigateBack -> back()

                ProductDetailsState.Event.EditedProduct -> {
                    showInfoMessage(
                        getString(Res.string.msg_menu_product_edited),
                        FAB_SNACKBAR_BOTTOM_PADDING,
                    )
                    back()
                }

                ProductDetailsState.Event.ShowAddProductError -> {
                    showErrorMessage(
                        getString(resource = Res.string.error_consumer_cart_add_product),
                    )
                }

                ProductDetailsState.Event.AddedProduct -> {
                    showInfoMessage(
                        getString(
                            Res.string.msg_menu_product_added,
                        ),
                        FAB_SNACKBAR_BOTTOM_PADDING,
                    )
                    back()
                }

                ProductDetailsState.Event.ShowFavoriteError -> {
                    showErrorMessage(
                        getString(resource = Res.string.error_something_went_wrong),
                    )
                }
            }
        }
        consumeEffects()
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun ProductDetailsScreen(
    menuProductName: String,
    menuProductUuid: String,
    additionUuidList: List<String>,
    productDetailsOpenedFrom: ProductDetailsOpenedFrom,
    cartProductUuid: String?,
    productDetailsViewState: ProductDetailsViewState,
    animatedContentScope: AnimatedVisibilityScope,
    onAction: (ProductDetailsState.Action) -> Unit,
) {
    FoodDeliveryScaffold(
        modifier = Modifier,
        title = menuProductName,
        backActionClick = {
            onAction(ProductDetailsState.Action.BackClick)
        },
        topActions =
            if (productDetailsViewState is ProductDetailsViewState.Success && productDetailsViewState.isAuthorized) {
                val isFavorite = productDetailsViewState.isFavorite
                persistentListOf(
                    FoodDeliveryAction(
                        iconId =
                            if (isFavorite) {
                                Res.drawable.ic_favorite_filled_24
                            } else {
                                Res.drawable.ic_favorite_24
                            },
                        tint =
                            if (isFavorite) {
                                FoodDeliveryTheme.colors.mainColors.primary
                            } else {
                                null
                            },
                        contentDescriptionId = Res.string.description_favorite,
                        onClick = {
                            onAction(ProductDetailsState.Action.FavoriteClick)
                        },
                    ),
                )
            } else {
                persistentListOf()
            },
        actionButton = {
            if (productDetailsViewState is ProductDetailsViewState.Success) {
                FoodDeliveryExtendedFab(
                    modifier =
                        Modifier
                            .widthIn(max = ProductDetailsMaxWidth)
                            .fillMaxWidth()
                            .padding(horizontal = FoodDeliveryTheme.dimensions.mediumSpace),
                    text =
                        stringResource(
                            resource = Res.string.action_product_details_want,
                            productDetailsViewState.menuProductUi.priceWithAdditions,
                        ),
                    onClick = {
                        onAction(
                            ProductDetailsState.Action.AddProductToCartClick(
                                productDetailsOpenedFrom = productDetailsOpenedFrom,
                                cartProductUuid = cartProductUuid,
                            ),
                        )
                    },
                    icon = Res.drawable.ic_plus_16,
                )
            }
        },
        backgroundColor = FoodDeliveryTheme.colors.mainColors.surface,
    ) {
        when (productDetailsViewState) {
            is ProductDetailsViewState.Success -> {
                ProductDetailsSuccessScreen(
                    menuProductUi = productDetailsViewState.menuProductUi,
                    onAction = onAction,
                    animatedContentScope = animatedContentScope,
                )
            }

            is ProductDetailsViewState.Loading -> {
                LoadingScreen()
            }

            is ProductDetailsViewState.Error -> {
                ErrorScreen(mainTextId = Res.string.common_error) {
                    onAction(
                        ProductDetailsState.Action.Init(
                            menuProductUuid = menuProductUuid,
                            selectedAdditionUuidList = additionUuidList,
                        ),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun ProductDetailsSuccessScreen(
    menuProductUi: ProductDetailsViewState.Success.MenuProductUi,
    animatedContentScope: AnimatedVisibilityScope,
    onAction: (ProductDetailsState.Action) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        LazyColumn(
            modifier =
                Modifier
                    .widthIn(max = ProductDetailsMaxWidth)
                    .fillMaxWidth(),
            contentPadding = PaddingValues(bottom = LocalBottomBarPadding.current),
        ) {
            item {
                ProductCard(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp),
                    menuProductUi = menuProductUi,
                    animatedContentScope = animatedContentScope,
                )
            }
            items(
                menuProductUi.additionList,
                key = { menuProductAdditionItem ->
                    menuProductAdditionItem.key
                },
            ) { menuProductAdditionItem ->
                when (menuProductAdditionItem) {
                    is AdditionItem.AdditionHeaderItem -> {
                        Text(
                            modifier =
                                Modifier
                                    .padding(horizontal = 16.dp)
                                    .padding(top = 24.dp),
                            text = menuProductAdditionItem.name,
                            style = FoodDeliveryTheme.typography.titleMedium.bold,
                            color = FoodDeliveryTheme.colors.mainColors.onSurface,
                        )
                    }

                    is AdditionItem.AdditionListItem -> {
                        FoodDeliveryItem(needDivider = !menuProductAdditionItem.product.isLast) {
                            AdditionRowItem(
                                uuid = menuProductAdditionItem.product.uuid,
                                groupId = menuProductAdditionItem.product.groupId,
                                photoLink = menuProductAdditionItem.product.photoLink,
                                name = menuProductAdditionItem.product.name,
                                price = menuProductAdditionItem.product.price,
                                isSelected = menuProductAdditionItem.product.isSelected,
                                isMultiple = menuProductAdditionItem.isMultiple,
                                onCardClick = { uuid, groupId ->
                                    onAction(
                                        ProductDetailsState.Action.AdditionClick(
                                            uuid = uuid,
                                            groupUuid = groupId,
                                        ),
                                    )
                                },
                            )
                        }
                    }

                    is AdditionItem.AdditionCardRowItem -> {
                        LazyRow(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(
                                items = menuProductAdditionItem.products,
                                key = { product -> product.uuid },
                            ) { product ->
                                AdditionCardItem(
                                    uuid = product.uuid,
                                    groupId = product.groupId,
                                    photoLink = product.photoLink,
                                    name = product.name,
                                    price = product.price,
                                    isSelected = product.isSelected,
                                    isMultiple = menuProductAdditionItem.isMultiple,
                                    onCardClick = { uuid, groupId ->
                                        onAction(
                                            ProductDetailsState.Action.AdditionClick(
                                                uuid = uuid,
                                                groupUuid = groupId,
                                            ),
                                        )
                                    },
                                )
                            }
                        }
                    }
                }
            }
            webSiteFooterItem(modifier = Modifier.padding(top = 24.dp))
            item {
                Spacer(modifier = Modifier.height(FoodDeliveryTheme.dimensions.scrollScreenBottomSpace))
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalAnimationApi::class)
@Composable
private fun ProductCard(
    menuProductUi: ProductDetailsViewState.Success.MenuProductUi,
    animatedContentScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    with(SharedTransitionScopeComposition.current) {
        Column(
            modifier = modifier,
        ) {
            FoodDeliveryAsyncImage(
                modifier =
                    Modifier
                        .sharedElement(
                            sharedContentState =
                                rememberSharedContentState(
                                    key = "image-${menuProductUi.uuid}",
                                ),
                            animatedVisibilityScope = animatedContentScope,
                        ).fillMaxWidth()
                        .clip(shape = RoundedCornerShape(size = 16.dp))
                        .heightIn(min = 228.dp),
                photoLink = menuProductUi.photoLink,
                contentDescription = stringResource(Res.string.description_product),
                contentScale = ContentScale.FillWidth,
            )
            Column(
                modifier =
                    Modifier
                        .padding(top = FoodDeliveryTheme.dimensions.mediumSpace),
            ) {
                Row {
                    Text(
                        modifier =
                            Modifier
                                .weight(1f)
                                .alignByBaseline()
                                .padding(end = FoodDeliveryTheme.dimensions.smallSpace)
                                .sharedElement(
                                    sharedContentState =
                                        rememberSharedContentState(
                                            key = "text-${menuProductUi.uuid}",
                                        ),
                                    animatedVisibilityScope = animatedContentScope,
                                ),
                        text = menuProductUi.name,
                        style = FoodDeliveryTheme.typography.titleMedium.bold,
                        color = FoodDeliveryTheme.colors.mainColors.onSurface,
                    )
                    Text(
                        modifier = Modifier.alignByBaseline(),
                        text = menuProductUi.size,
                        style = FoodDeliveryTheme.typography.bodySmall,
                        color = FoodDeliveryTheme.colors.mainColors.onSurfaceVariant,
                    )
                }
                Row(
                    modifier =
                        Modifier
                            .padding(top = FoodDeliveryTheme.dimensions.smallSpace),
                ) {
                    menuProductUi.oldPrice?.let {
                        Text(
                            modifier =
                                Modifier
                                    .padding(end = FoodDeliveryTheme.dimensions.smallSpace)
                                    .sharedElement(
                                        sharedContentState =
                                            rememberSharedContentState(
                                                key = "oldPrice-${menuProductUi.uuid}",
                                            ),
                                        animatedVisibilityScope = animatedContentScope,
                                    ),
                            text = menuProductUi.oldPrice,
                            style = FoodDeliveryTheme.typography.bodyLarge,
                            color = FoodDeliveryTheme.colors.mainColors.onSurfaceVariant,
                            textDecoration = TextDecoration.LineThrough,
                        )
                    }
                    Text(
                        modifier =
                            Modifier.sharedElement(
                                sharedContentState =
                                    rememberSharedContentState(
                                        key = "price-${menuProductUi.uuid}",
                                    ),
                                animatedVisibilityScope = animatedContentScope,
                            ),
                        text = menuProductUi.newPrice,
                        style = FoodDeliveryTheme.typography.bodyLarge.bold,
                        color = FoodDeliveryTheme.colors.mainColors.onSurface,
                    )
                }

                Text(
                    modifier =
                        Modifier
                            .padding(top = FoodDeliveryTheme.dimensions.mediumSpace),
                    text = menuProductUi.description,
                    style = FoodDeliveryTheme.typography.bodyLarge,
                    color = FoodDeliveryTheme.colors.mainColors.onSurface,
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true)
@Composable
private fun ProductDetailsSuccessScreenPreview() {
    FoodDeliveryTheme {
        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
                ProductDetailsScreen(
                    animatedContentScope = this,
                    menuProductName = "Бэргер куриный Макс с экстра сырным соусом",
                    menuProductUuid = "",
                    productDetailsViewState =
                        ProductDetailsViewState.Success(
                            topCartUi =
                                TopCartUi(
                                    cost = "100",
                                    count = "2",
                                ),
                            isAuthorized = true,
                            isFavorite = true,
                            menuProductUi =
                                ProductDetailsViewState.Success.MenuProductUi(
                                    photoLink = "",
                                    name = "Бэргер куриный Макс с экстра сырным соусом",
                                    size = "300 г",
                                    oldPrice = "320 ₽",
                                    newPrice = "280 ₽",
                                    description =
                                        "Сочная котлетка, сыр Чедр, маринованный огурчик, помидор, " +
                                            "красный лук, салат, фирменный соус, булочка с кунжутом",
                                    additionList =
                                        persistentListOf(
                                            AdditionItem.AdditionHeaderItem(
                                                key = "key1",
                                                uuid = "uuid1",
                                                name = "Булочка",
                                            ),
                                            AdditionItem.AdditionListItem(
                                                key = "key2",
                                                product =
                                                    MenuProductAdditionItem(
                                                        uuid = "uuid2",
                                                        isSelected = true,
                                                        name = "БулОЧКА Валентина",
                                                        price = "+200",
                                                        photoLink = "",
                                                        isLast = false,
                                                        groupId = "",
                                                    ),
                                                isMultiple = false,
                                            ),
                                            AdditionItem.AdditionListItem(
                                                key = "key3",
                                                product =
                                                    MenuProductAdditionItem(
                                                        uuid = "uuid3",
                                                        isSelected = false,
                                                        name = "БулОЧКА Марка",
                                                        price = "300",
                                                        photoLink = "",
                                                        isLast = true,
                                                        groupId = "",
                                                    ),
                                                isMultiple = false,
                                            ),
                                            AdditionItem.AdditionHeaderItem(
                                                key = "key4",
                                                uuid = "uuid4",
                                                name = "Добавить по вкусу",
                                            ),
                                            AdditionItem.AdditionCardRowItem(
                                                key = "key-card-row",
                                                products =
                                                    persistentListOf(
                                                        MenuProductAdditionItem(
                                                            uuid = "uuid5",
                                                            isSelected = true,
                                                            name = "Монкейэс",
                                                            price = "13",
                                                            photoLink = "",
                                                            isLast = false,
                                                            groupId = "uuid4",
                                                        ),
                                                        MenuProductAdditionItem(
                                                            uuid = "uuid6",
                                                            isSelected = false,
                                                            name = "Лида в лаваше",
                                                            price = "2",
                                                            photoLink = "",
                                                            isLast = false,
                                                            groupId = "uuid4",
                                                        ),
                                                        MenuProductAdditionItem(
                                                            uuid = "uuid7",
                                                            isSelected = false,
                                                            name = "Огурец",
                                                            price = "5",
                                                            photoLink = "",
                                                            isLast = false,
                                                            groupId = "uuid4",
                                                        ),
                                                        MenuProductAdditionItem(
                                                            uuid = "uuid8",
                                                            isSelected = false,
                                                            name = "Соус барбекю",
                                                            price = "10",
                                                            photoLink = "",
                                                            isLast = true,
                                                            groupId = "uuid4",
                                                        ),
                                                    ),
                                                isMultiple = true,
                                            ),
                                        ),
                                    priceWithAdditions = "300 ₽",
                                    uuid = "uid",
                                ),
                        ),
                    additionUuidList = persistentListOf(),
                    onAction = {},
                    productDetailsOpenedFrom = ProductDetailsOpenedFrom.CART_PRODUCT,
                    cartProductUuid = "",
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true)
@Composable
private fun ProductDetailsLoadingScreenPreview() {
    FoodDeliveryTheme {
        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
                ProductDetailsScreen(
                    menuProductName = "Бэргер куриный Макс с экстра сырным соусом",
                    menuProductUuid = "",
                    productDetailsOpenedFrom = ProductDetailsOpenedFrom.CART_PRODUCT,
                    cartProductUuid = "",
                    productDetailsViewState = ProductDetailsViewState.Loading,
                    additionUuidList = persistentListOf(),
                    onAction = {},
                    animatedContentScope = this,
                )
            }
        }
    }
}
