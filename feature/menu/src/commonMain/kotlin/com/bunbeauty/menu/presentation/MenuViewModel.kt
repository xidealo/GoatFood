package com.bunbeauty.menu.presentation

import com.bunbeauty.analytic.AnalyticService
import com.bunbeauty.analytic.event.menu.AddMenuProductClickEvent
import com.bunbeauty.analytic.event.menu.LoadedMenuEvent
import com.bunbeauty.analytic.parameter.MenuProductUuidEventParameter
import com.bunbeauty.analytic.parameter.TimeParameter
import com.bunbeauty.core.Logger
import com.bunbeauty.core.base.SharedStateViewModel
import com.bunbeauty.core.domain.ObserveCartUseCase
import com.bunbeauty.core.domain.auth.ObserveTokenUseCase
import com.bunbeauty.core.domain.discount.GetDiscountUseCase
import com.bunbeauty.core.domain.favorite.GetFavoriteMenuProductsUseCase
import com.bunbeauty.core.domain.menu_product.AddMenuProductUseCase
import com.bunbeauty.core.domain.menu_product.IMenuProductInteractor
import com.bunbeauty.core.domain.order.GetLastDeliveredOrderUseCase
import com.bunbeauty.core.domain.order.GetLastOrderUseCase
import com.bunbeauty.core.domain.order.ObserveLastOrderUseCase
import com.bunbeauty.core.domain.order.StopObserveOrdersUseCase
import com.bunbeauty.core.domain.settings.RefreshSettingsUseCase
import com.bunbeauty.core.extension.launchSafe
import com.bunbeauty.core.model.CategoryItem
import com.bunbeauty.core.model.Discount
import com.bunbeauty.core.model.MenuItem
import com.bunbeauty.core.model.mapper.toMenuItemList
import com.bunbeauty.core.model.mapper.toMenuProductItem
import com.bunbeauty.core.model.menu.MenuSection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlin.time.measureTime

private const val MAIN_MENU_VIEW_MODEL_TAG = "MenuViewModel"

private fun Discount?.toMenuDiscountItem(): MenuItem.Discount? {
    val discount = this ?: return null
    val percent = discount.firstOrderDiscount ?: return null
    return MenuItem.Discount(
        discount = percent.toString(),
        source = discount.source,
    )
}

class MenuViewModel(
    private val menuProductInteractor: IMenuProductInteractor,
    private val observeCartUseCase: ObserveCartUseCase,
    private val addMenuProductUseCase: AddMenuProductUseCase,
    private val getDiscountUseCase: GetDiscountUseCase,
    private val refreshSettingsUseCase: RefreshSettingsUseCase,
    private val analyticService: AnalyticService,
    private val observeLastOrderUseCase: ObserveLastOrderUseCase,
    private val stopObserveOrdersUseCase: StopObserveOrdersUseCase,
    private val getLastOrderUseCase: GetLastOrderUseCase,
    private val observeTokenUseCase: ObserveTokenUseCase,
    private val getFavoriteMenuProductsUseCase: GetFavoriteMenuProductsUseCase,
    private val getLastDeliveredOrderUseCase: GetLastDeliveredOrderUseCase,
) : SharedStateViewModel<MenuState.DataState, MenuState.Action, MenuState.Event>(
        initDataState =
            MenuState.DataState(
                categoryItemList = emptyList(),
                cartCostAndCount = null,
                menuItemList = emptyList(),
                state = MenuState.DataState.State.LOADING,
                userScrollEnabled = true,
                lastOrder = null,
            ),
    ) {
    private var selectedCategoryUuid: String? = null
    private var currentMenuPosition = 0

    private var observeLastOrderJob: Job? = null
    private var orderObservationUuid: String? = null
    private var isAppReviewChecked = false

    init {
        getMenu()
        observeCart()
        observeToken()
        sharedScope.launch {
            val lastOrder = getLastOrderUseCase()
            setState {
                copy(lastOrder = lastOrder)
            }
        }
    }

    override fun reduce(
        action: MenuState.Action,
        dataState: MenuState.DataState,
    ) {
        when (action) {
            MenuState.Action.OnRefreshClicked -> getMenu()
            is MenuState.Action.OnCategoryClicked -> setCategory(action.categoryItem.uuid)
            is MenuState.Action.OnMenuPositionChanged -> onMenuPositionChanged(action.menuPosition)
            MenuState.Action.OnStartAutoScroll -> onStartAutoScroll()
            MenuState.Action.OnStopAutoScroll -> onStopAutoScroll()
            is MenuState.Action.OnAddProductClicked -> onAddProductClicked(action.menuProductUuid)
            is MenuState.Action.OnMenuItemClicked -> onMenuItemClicked(action.menuProductUuid)
            is MenuState.Action.OnLastOrderClicked -> onLastOrderClicked(action.uuid)
            MenuState.Action.OnProfileClicked -> onProfileClicked()
            MenuState.Action.OnCartClicked -> onCartClicked()
            MenuState.Action.StartLastOrderObservation -> startLastOrderObservation()
            MenuState.Action.StopLastOrderObservation -> stopLastOrderObservation()
            MenuState.Action.RefreshDiscountAndFavorites -> refreshDiscountAndFavoritesOnStart()
            MenuState.Action.ScrollToTop -> scrollToTop()
        }
    }

    private fun scrollToTop() {
        currentMenuPosition = 0
        mutableDataState.value
            .categoryItemList
            .firstOrNull()
            ?.uuid
            ?.let { categoryUuid ->
                setCategory(categoryUuid)
            }
        setState {
            copy(scrollToTopRequest = scrollToTopRequest + 1)
        }
    }

    private fun onStartAutoScroll() {
        setState {
            copy(userScrollEnabled = false)
        }
    }

    private fun onStopAutoScroll() {
        setState {
            copy(userScrollEnabled = true)
        }
    }

    private fun observeCart() {
        sharedScope.launchSafe(
            block = {
                observeCartUseCase().collectLatest { cartTotalAndCount ->
                    setState {
                        copy(cartCostAndCount = cartTotalAndCount)
                    }
                }
            },
            onError = { throwable ->
                handleError(throwable)
            },
        )
    }

    private fun observeToken() {
        sharedScope.launchSafe(
            block = {
                observeTokenUseCase()
                    .drop(1)
                    .distinctUntilChanged()
                    .collectLatest {
                        refreshDiscountAndFavorites()
                    }
            },
            onError = { throwable ->
                Logger.logE(MAIN_MENU_VIEW_MODEL_TAG, throwable.stackTraceToString())
            },
        )
    }

    private suspend fun refreshDiscountAndFavorites() {
        refreshSettingsUseCase()
        val discountItem = getDiscountUseCase().toMenuDiscountItem()
        val favoritesItem = toFavoritesMenuItem(loadFavoriteProductList())
        val menuSectionList = menuProductInteractor.getMenuSectionList()

        setState {
            copy(
                menuItemList =
                    buildMenuItemListPrefix(
                        discountItem = discountItem,
                        favoritesItem = favoritesItem,
                    ) +
                        menuItemList.filterNot { menuItem ->
                            menuItem is MenuItem.Discount || menuItem is MenuItem.Favorites
                        },
                categoryItemList =
                    buildCategoryItemList(
                        menuSectionList = menuSectionList,
                    ),
            )
        }
    }

    private fun getMenu() {
        Logger.logD(MAIN_MENU_VIEW_MODEL_TAG, "getMenu")

        setState {
            copy(state = MenuState.DataState.State.LOADING)
        }

        sharedScope.launchSafe(
            block = {
                val time =
                    measureTime {
                        val menuSectionList = menuProductInteractor.getMenuSectionList()
                        val favoritesItem = toFavoritesMenuItem(loadFavoriteProductList())

                        if (selectedCategoryUuid == null) {
                            selectedCategoryUuid = menuSectionList.firstOrNull()?.category?.uuid
                        }

                        val discountItem =
                            run {
                                refreshSettingsUseCase()
                                getDiscountUseCase().toMenuDiscountItem()
                            }
                        val menuItemList =
                            buildMenuItemListPrefix(
                                discountItem = discountItem,
                                favoritesItem = favoritesItem,
                            ) +
                                menuSectionList.flatMap { menuSection ->
                                    menuSection.toMenuItemList()
                                }
                        setState {
                            copy(
                                categoryItemList =
                                    buildCategoryItemList(
                                        menuSectionList = menuSectionList,
                                    ),
                                menuItemList = menuItemList,
                                state = MenuState.DataState.State.SUCCESS,
                            )
                        }
                    }
                analyticService.sendEvent(
                    event =
                        LoadedMenuEvent(
                            timeParameter =
                                TimeParameter(
                                    value = time.toString(),
                                ),
                        ),
                )
                checkAppReview()
            },
            onError = { throwable ->
                handleError(throwable)
            },
        )
    }

    private fun checkAppReview() {
        if (isAppReviewChecked) {
            return
        }
        isAppReviewChecked = true
        sharedScope.launchSafe(
            block = {
                getLastDeliveredOrderUseCase()?.let { order ->
                    addEvent {
                        MenuState.Event.RequestAppReview(orderUuid = order.uuid)
                    }
                }
            },
            onError = { throwable ->
                Logger.logE(MAIN_MENU_VIEW_MODEL_TAG, throwable.stackTraceToString())
            },
        )
    }

    private fun onMenuPositionChanged(menuPosition: Int) {
        if (!mutableDataState.value.userScrollEnabled || menuPosition == currentMenuPosition) {
            return
        }

        currentMenuPosition = menuPosition

        if (menuPosition < menuContentStartGridIndex()) {
            return
        }

        val menuListPosition =
            (menuPosition - menuContentStartGridIndex())
                .coerceAtLeast(0)

        sharedScope.launchSafe(
            block = {
                val menuItemModelList = mutableDataState.value.menuItemList
                menuItemModelList
                    .filterIsInstance<MenuItem.CategoryHeader>()
                    .findLast { menuItemModel ->
                        menuItemModelList.indexOf(menuItemModel) <= menuListPosition
                    }?.let { menuItemModel ->
                        setCategory(menuItemModel.uuid)
                    }
            },
            onError = { throwable ->
                handleError(throwable)
            },
        )
    }

    private fun handleError(throwable: Throwable) {
        Logger.logE(MAIN_MENU_VIEW_MODEL_TAG, throwable.stackTraceToString())
        setState {
            copy(state = MenuState.DataState.State.ERROR)
        }
    }

    private fun onMenuItemClicked(menuProductUuid: String) {
        val menuProduct = findMenuProduct(uuid = menuProductUuid) ?: return

        addEvent {
            MenuState.Event.GoToSelectedItem(
                uuid = menuProduct.uuid,
                name = menuProduct.name,
            )
        }
    }

    private fun onLastOrderClicked(uuid: String) {
        addEvent {
            MenuState.Event.OpenOrderDetails(uuid)
        }
    }

    private fun onProfileClicked() {
        addEvent {
            MenuState.Event.OpenProfile
        }
    }

    private fun onCartClicked() {
        addEvent {
            MenuState.Event.OpenConsumerCart
        }
    }

    private fun findMenuProduct(uuid: String): MenuItem.Product? {
        val menuItemList = mutableDataState.value.menuItemList
        val favoriteProduct =
            menuItemList
                .filterIsInstance<MenuItem.Favorites>()
                .flatMap { favorites -> favorites.products }
                .find { menuProduct ->
                    menuProduct.uuid == uuid
                }
        if (favoriteProduct != null) {
            return favoriteProduct
        }
        return menuItemList
            .filterIsInstance<MenuItem.Product>()
            .find { menuProduct ->
                menuProduct.uuid == uuid
            }
    }

    private fun onAddProductClicked(menuProductUuid: String) {
        val menuProduct = findMenuProduct(uuid = menuProductUuid) ?: return

        analyticService.sendEvent(
            event =
                AddMenuProductClickEvent(
                    menuProductUuidEventParameter = MenuProductUuidEventParameter(value = menuProduct.uuid),
                ),
        )
        sharedScope.launchSafe(
            block = {
                if (menuProduct.hasAdditions) {
                    addEvent {
                        MenuState.Event.GoToSelectedItem(
                            uuid = menuProduct.uuid,
                            name = menuProduct.name,
                        )
                    }
                } else {
                    addMenuProductUseCase(menuProductUuid = menuProduct.uuid)
                    addEvent {
                        MenuState.Event.ShowAddedProduct(name = menuProduct.name)
                    }
                }
            },
            onError = {
                addEvent {
                    MenuState.Event.ShowAddProductError
                }
            },
        )
    }

    private fun setCategory(categoryUuid: String) {
        sharedScope.launch {
            if (selectedCategoryUuid == categoryUuid) {
                return@launch
            }
            selectedCategoryUuid = categoryUuid

            val categoryItemModelList =
                mutableDataState.value.categoryItemList.map { categoryItemModel ->
                    when {
                        categoryItemModel.isSelected -> {
                            categoryItemModel.copy(isSelected = false)
                        }

                        categoryItemModel.uuid == selectedCategoryUuid -> {
                            categoryItemModel.copy(isSelected = true)
                        }

                        else -> {
                            categoryItemModel
                        }
                    }
                }
            setState {
                copy(
                    categoryItemList = categoryItemModelList,
                    menuItemList = menuItemList,
                )
            }
        }
    }

    private fun toCategoryItemModel(menuSection: MenuSection): CategoryItem =
        CategoryItem(
            key = "CategoryItemModel ${menuSection.category.uuid}",
            uuid = menuSection.category.uuid,
            name = menuSection.category.name,
            isSelected = isCategorySelected(menuSection.category.uuid),
        )

    private fun buildCategoryItemList(menuSectionList: List<MenuSection>): List<CategoryItem> =
        menuSectionList.map { menuSection ->
            toCategoryItemModel(menuSection)
        }

    private suspend fun loadFavoriteProductList(): List<MenuItem.Product> =
        getFavoriteMenuProductsUseCase().map { menuProduct ->
            menuProduct.toMenuProductItem()
        }

    private fun toFavoritesMenuItem(products: List<MenuItem.Product>): MenuItem.Favorites? =
        if (products.isNotEmpty()) {
            MenuItem.Favorites(products = products)
        } else {
            null
        }

    private fun buildMenuItemListPrefix(
        discountItem: MenuItem.Discount?,
        favoritesItem: MenuItem.Favorites?,
    ): List<MenuItem> = listOfNotNull(discountItem, favoritesItem)

    private fun refreshDiscountAndFavoritesOnStart() {
        sharedScope.launchSafe(
            block = {
                refreshDiscountAndFavorites()
            },
            onError = { throwable ->
                Logger.logE(MAIN_MENU_VIEW_MODEL_TAG, throwable.stackTraceToString())
            },
        )
    }

    private fun isCategorySelected(categoryUuid: String): Boolean =
        selectedCategoryUuid?.let {
            selectedCategoryUuid == categoryUuid
        } ?: false

    private fun startLastOrderObservation() {
        if (observeLastOrderJob?.isActive == true) {
            return
        }
        observeLastOrderJob =
            sharedScope.launchSafe(
                block = {
                    val (uuid, lastOrderFlow) = observeLastOrderUseCase()
                    orderObservationUuid = uuid
                    lastOrderFlow.collectLatest { lightOrder ->
                        setState {
                            copy(lastOrder = lightOrder)
                        }
                    }
                },
                onError = { error ->
                    Logger.logE(MAIN_MENU_VIEW_MODEL_TAG, error.stackTraceToString())
                },
                dispatcher = Dispatchers.Default,
            )
    }

    private fun stopLastOrderObservation() {
        observeLastOrderJob?.cancel()
        orderObservationUuid?.let { uuid ->
            sharedScope.launch(Dispatchers.Default) {
                stopObserveOrdersUseCase(uuid)
            }
        }
        orderObservationUuid = null
    }
}
