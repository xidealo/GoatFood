package com.bunbeauty.menu.presentation

import com.bunbeauty.analytic.CompositeAnalyticService
import com.bunbeauty.core.domain.GetCartProductCountUseCase
import com.bunbeauty.core.domain.GetNewTotalCostUseCase
import com.bunbeauty.core.domain.ObserveCartUseCase
import com.bunbeauty.core.domain.auth.ObserveTokenUseCase
import com.bunbeauty.core.domain.discount.GetDiscountUseCase
import com.bunbeauty.core.domain.favorite.GetFavoriteMenuProductsUseCase
import com.bunbeauty.core.domain.menu_product.AddMenuProductUseCase
import com.bunbeauty.core.domain.menu_product.GetMenuProductListUseCase
import com.bunbeauty.core.domain.menu_product.IMenuProductInteractor
import com.bunbeauty.core.domain.order.GetLastDeliveredOrderUseCase
import com.bunbeauty.core.domain.order.GetLastOrderUseCase
import com.bunbeauty.core.domain.order.LightOrderMapper
import com.bunbeauty.core.domain.order.ObserveLastOrderUseCase
import com.bunbeauty.core.domain.order.StopObserveOrdersUseCase
import com.bunbeauty.core.domain.order.TakeInProgressLightOrderUseCaseImpl
import com.bunbeauty.core.domain.repo.CartProductRepo
import com.bunbeauty.core.domain.repo.FavoriteRepo
import com.bunbeauty.core.domain.repo.OrderRepo
import com.bunbeauty.core.domain.repo.SettingsRepo
import com.bunbeauty.core.domain.repo.UserRepo
import com.bunbeauty.core.domain.settings.RefreshSettingsUseCase
import com.bunbeauty.core.model.date_time.Date
import com.bunbeauty.core.model.date_time.DateTime
import com.bunbeauty.core.model.date_time.Time
import com.bunbeauty.core.model.order.LightOrder
import com.bunbeauty.core.model.order.OrderStatus
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MenuViewModelTest {
    private val menuProductInteractor = mock<IMenuProductInteractor>()
    private val cartProductRepo = mock<CartProductRepo>()
    private val getNewTotalCostUseCase = mock<GetNewTotalCostUseCase>()
    private val getCartProductCountUseCase = mock<GetCartProductCountUseCase>()
    private val getDiscountUseCase = mock<GetDiscountUseCase>()
    private val settingsRepo = mock<SettingsRepo>()
    private val orderRepo = mock<OrderRepo>()
    private val userRepo = mock<UserRepo>()
    private val favoriteRepo = mock<FavoriteRepo>()
    private val getMenuProductListUseCase = mock<GetMenuProductListUseCase>()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `delivered last order creates app review event after menu is loaded`() =
        runTest {
            val deliveredOrder = lightOrder(status = OrderStatus.DELIVERED)
            setupMenu(lastDeliveredOrder = deliveredOrder)

            val viewModel = createViewModel()

            assertEquals(MenuState.DataState.State.SUCCESS, viewModel.dataState.value.state)
            assertEquals(
                listOf(MenuState.Event.RequestAppReview(orderUuid = deliveredOrder.uuid)),
                viewModel.events.value,
            )
        }

    @Test
    fun `menu refresh does not repeat app review check`() =
        runTest {
            setupMenu(lastDeliveredOrder = lightOrder(status = OrderStatus.DELIVERED))
            val viewModel = createViewModel()

            viewModel.onAction(MenuState.Action.OnRefreshClicked)

            verifySuspend(mode = VerifyMode.exactly(1)) {
                orderRepo.getLastOrderByUserUuidNetworkFirst()
            }
            assertEquals(1, viewModel.events.value.count { it is MenuState.Event.RequestAppReview })
        }

    @Test
    fun `active last order does not create app review event`() =
        runTest {
            setupMenu(lastDeliveredOrder = null)

            val viewModel = createViewModel()

            assertTrue(viewModel.events.value.isEmpty())
        }

    private suspend fun setupMenu(lastDeliveredOrder: LightOrder?) {
        everySuspend { menuProductInteractor.getMenuSectionList() } returns emptyList()
        everySuspend { settingsRepo.refreshSettings() } returns null
        everySuspend { getDiscountUseCase() } returns null
        everySuspend { orderRepo.getLastOrderByUserUuidLocalFirst() } returns null
        everySuspend { orderRepo.getLastOrderByUserUuidNetworkFirst() } returns lastDeliveredOrder
        everySuspend { userRepo.getToken() } returns null
        every { userRepo.token } returns emptyFlow()
        every { cartProductRepo.observeCartProductList() } returns emptyFlow()
    }

    private fun createViewModel(): MenuViewModel {
        val takeInProgressLightOrderUseCase = TakeInProgressLightOrderUseCaseImpl()
        return MenuViewModel(
            menuProductInteractor = menuProductInteractor,
            observeCartUseCase =
                ObserveCartUseCase(
                    cartProductRepo = cartProductRepo,
                    getNewTotalCostUseCase = getNewTotalCostUseCase,
                ),
            addMenuProductUseCase =
                AddMenuProductUseCase(
                    getCartProductCountUseCase = getCartProductCountUseCase,
                    cartProductRepo = cartProductRepo,
                ),
            getDiscountUseCase = getDiscountUseCase,
            refreshSettingsUseCase = RefreshSettingsUseCase(settingsRepo = settingsRepo),
            analyticService = CompositeAnalyticService(services = emptyList()),
            observeLastOrderUseCase =
                ObserveLastOrderUseCase(
                    orderRepo = orderRepo,
                    lightOrderMapper = LightOrderMapper(),
                    takeInProgressLightOrderUseCase = takeInProgressLightOrderUseCase,
                ),
            stopObserveOrdersUseCase = StopObserveOrdersUseCase(orderRepo = orderRepo),
            getLastOrderUseCase =
                GetLastOrderUseCase(
                    orderRepo = orderRepo,
                    takeInProgressLightOrderUseCase = takeInProgressLightOrderUseCase,
                ),
            observeTokenUseCase = ObserveTokenUseCase(userRepo = userRepo),
            getFavoriteMenuProductsUseCase =
                GetFavoriteMenuProductsUseCase(
                    favoriteRepo = favoriteRepo,
                    getMenuProductListUseCase = getMenuProductListUseCase,
                    userRepo = userRepo,
                ),
            getLastDeliveredOrderUseCase = GetLastDeliveredOrderUseCase(orderRepo = orderRepo),
        )
    }

    private fun lightOrder(status: OrderStatus) =
        LightOrder(
            uuid = "order-uuid",
            status = status,
            code = "01",
            dateTime = DateTime(Date(1, 2, 3), Time(20, 20)),
        )
}
