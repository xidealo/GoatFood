package com.bunbeauty.menu.presentation

import com.bunbeauty.core.base.BaseAction
import com.bunbeauty.core.base.BaseDataState
import com.bunbeauty.core.base.BaseEvent
import com.bunbeauty.core.model.CategoryItem
import com.bunbeauty.core.model.MenuItem
import com.bunbeauty.core.model.cart.CartCostAndCount
import com.bunbeauty.core.model.order.LightOrder

interface MenuState {
    data class DataState(
        val categoryItemList: List<CategoryItem>,
        val cartCostAndCount: CartCostAndCount?,
        val menuItemList: List<MenuItem>,
        val state: State,
        val userScrollEnabled: Boolean,
        val lastOrder: LightOrder?,
        val scrollToTopRequest: Int = 0,
    ) : BaseDataState {
        enum class State {
            LOADING,
            SUCCESS,
            ERROR,
        }
    }

    sealed interface Action : BaseAction {
        data object OnRefreshClicked : Action

        data class OnCategoryClicked(
            val categoryItem: CategoryItem,
        ) : Action

        data class OnMenuPositionChanged(
            val menuPosition: Int,
        ) : Action

        data object OnStartAutoScroll : Action

        data object OnStopAutoScroll : Action

        data class OnAddProductClicked(
            val menuProductUuid: String,
        ) : Action

        data class OnMenuItemClicked(
            val menuProductUuid: String,
        ) : Action

        data class OnLastOrderClicked(
            val uuid: String,
        ) : Action

        data object OnProfileClicked : Action

        data object OnCartClicked : Action

        data object StartLastOrderObservation : Action

        data object StopLastOrderObservation : Action

        data object RefreshDiscountAndFavorites : Action

        data object ScrollToTop : Action
    }

    sealed interface Event : BaseEvent {
        data class GoToSelectedItem(
            val uuid: String,
            val name: String,
        ) : Event

        data object ShowAddProductError : Event

        data class ShowAddedProduct(
            val name: String,
        ) : Event

        data class OpenOrderDetails(
            val uuid: String,
        ) : Event

        data object OpenProfile : Event

        data object OpenConsumerCart : Event

        data class RequestAppReview(
            val orderUuid: String,
        ) : Event
    }
}
