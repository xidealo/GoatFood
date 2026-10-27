package com.bunbeauty.createorder.presentation.createorder

import com.bunbeauty.analytic.AnalyticService
import com.bunbeauty.analytic.event.order.OrderCreateClickEvent
import com.bunbeauty.analytic.event.order.OrderCreateFailEvent
import com.bunbeauty.analytic.event.order.OrderCreateSuccessEvent
import com.bunbeauty.analytic.event.order.OrderDeliveryToggleEvent
import com.bunbeauty.core.Constants.PERCENT
import com.bunbeauty.core.Constants.RUBLE_CURRENCY
import com.bunbeauty.core.Logger
import com.bunbeauty.core.base.SharedStateViewModel
import com.bunbeauty.core.domain.address.GetCurrentUserAddressUseCase
import com.bunbeauty.core.domain.address.GetSelectableUserAddressListUseCase
import com.bunbeauty.core.domain.address.SaveSelectedUserAddressUseCase
import com.bunbeauty.core.domain.cafe.GetAdditionalUtensilsUseCase
import com.bunbeauty.core.domain.cafe.GetDeferredTimeHintUseCase
import com.bunbeauty.core.domain.cafe.GetSelectableCafeListUseCase
import com.bunbeauty.core.domain.cafe.GetWorkloadCafeUseCase
import com.bunbeauty.core.domain.cafe.HasOpenedCafeUseCase
import com.bunbeauty.core.domain.cafe.ICafeInteractor
import com.bunbeauty.core.domain.cafe.IsDeliveryEnabledFromCafeUseCase
import com.bunbeauty.core.domain.cafe.IsPickupEnabledFromCafeUseCase
import com.bunbeauty.core.domain.cart.GetCartTotalFlowUseCase
import com.bunbeauty.core.domain.cart.ICartProductInteractor
import com.bunbeauty.core.domain.city.GetSelectedCityTimeZoneUseCase
import com.bunbeauty.core.domain.deferred_time.GetMinTimeUseCase
import com.bunbeauty.core.domain.exeptions.NotAllowedTimeForOrderException
import com.bunbeauty.core.domain.exeptions.OrderNotAvailableException
import com.bunbeauty.core.domain.motivation.GetMotivationUseCase
import com.bunbeauty.core.domain.order.CreateOrderUseCase
import com.bunbeauty.core.domain.order.ExtendedComment
import com.bunbeauty.core.domain.order.GetExtendedCommentUseCase
import com.bunbeauty.core.domain.order.GetWithoutUtensilsUseCase
import com.bunbeauty.core.domain.order.SaveWithoutUtensilsUseCase
import com.bunbeauty.core.domain.payment.GetSelectablePaymentMethodListUseCase
import com.bunbeauty.core.domain.payment.GetSelectedPaymentMethodUseCase
import com.bunbeauty.core.domain.payment.SavePaymentMethodUseCase
import com.bunbeauty.core.domain.user.IUserInteractor
import com.bunbeauty.core.extension.launchSafe
import com.bunbeauty.core.model.cafe.Cafe
import com.bunbeauty.core.model.date_time.Time
import com.bunbeauty.core.model.order.PickupServing
import com.bunbeauty.core.motivation.toMotivationData
import kotlinx.coroutines.Job

private const val DELIVERY_POSITION = 0
private const val CREATION_ORDER_VIEW_MODEL_TAG = "CreateOrderViewModel"

class CreateOrderViewModel(
    private val cartProductInteractor: ICartProductInteractor,
    private val cafeInteractor: ICafeInteractor,
    private val userInteractor: IUserInteractor,
    private val getSelectableUserAddressList: GetSelectableUserAddressListUseCase,
    private val getCurrentUserAddressUseCase: GetCurrentUserAddressUseCase,
    private val getSelectableCafeList: GetSelectableCafeListUseCase,
    private val getCartTotalFlowUseCase: GetCartTotalFlowUseCase,
    private val getMotivationUseCase: GetMotivationUseCase,
    private val getMinTime: GetMinTimeUseCase,
    private val createOrder: CreateOrderUseCase,
    private val getSelectedCityTimeZone: GetSelectedCityTimeZoneUseCase,
    private val saveSelectedUserAddress: SaveSelectedUserAddressUseCase,
    private val getSelectablePaymentMethodListUseCase: GetSelectablePaymentMethodListUseCase,
    private val savePaymentMethodUseCase: SavePaymentMethodUseCase,
    private val isDeliveryEnabledFromCafeUseCase: IsDeliveryEnabledFromCafeUseCase,
    private val isPickupEnabledFromCafeUseCase: IsPickupEnabledFromCafeUseCase,
    private val hasOpenedCafeUseCase: HasOpenedCafeUseCase,
    private val getWorkloadCafeUseCase: GetWorkloadCafeUseCase,
    private val getSelectedPaymentMethodUseCase: GetSelectedPaymentMethodUseCase,
    private val getExtendedCommentUseCase: GetExtendedCommentUseCase,
    private val getWithoutUtensilsUseCase: GetWithoutUtensilsUseCase,
    private val saveWithoutUtensilsUseCase: SaveWithoutUtensilsUseCase,
    private val getAdditionalUtensilsUseCase: GetAdditionalUtensilsUseCase,
    private val getDeferredTimeHintUseCase: GetDeferredTimeHintUseCase,
    private val analyticService: AnalyticService,
) : SharedStateViewModel<CreateOrder.DataState, CreateOrder.Action, CreateOrder.Event>(
        initDataState =
            CreateOrder.DataState(
                isDelivery = true,
                userAddressList = emptyList(),
                selectedUserAddress = null,
                isAddressErrorShown = CreateOrder.DataState.AddressErrorState.INIT,
                cafeList = emptyList(),
                selectedCafe = null,
                comment = "",
                deferredTime = CreateOrder.DeferredTime.Asap,
                paymentMethodList = emptyList(),
                selectedPaymentMethod = null,
                cartTotal = CreateOrder.CartTotal.Loading,
                isLoading = true,
                deliveryState = CreateOrder.DataState.DeliveryState.ENABLED,
                isPickupEnabled = true,
                hasOpenedCafe = true,
                workload = Cafe.Workload.LOW,
                additionalUtensils = false,
                additionalUtensilsCount = "",
            ),
    ) {
    private var getCartTotalJob: Job? = null

    override fun reduce(
        action: CreateOrder.Action,
        dataState: CreateOrder.DataState,
    ) {
        when (action) {
            CreateOrder.Action.Init -> {
                loadData(isDelivery = dataState.isDelivery)
            }

            is CreateOrder.Action.ChangeMethod -> {
                changeMethod(position = action.position)
            }

            CreateOrder.Action.DeliveryAddressClick -> {
                userAddressClick()
            }

            CreateOrder.Action.AddAddressClick -> {
                addAddressClick()
            }

            CreateOrder.Action.HideDeliveryAddressList -> {
                hideUserAddressList()
            }

            is CreateOrder.Action.ChangeDeliveryAddress -> {
                changeUserAddress(
                    userAddressUuid = action.addressUuid,
                    isDelivery = dataState.isDelivery,
                )
            }

            CreateOrder.Action.PickupAddressClick -> {
                cafeAddressClick()
            }

            CreateOrder.Action.HidePickupAddressList -> {
                hideCafeList()
            }

            is CreateOrder.Action.ChangePickupAddress -> {
                changeCafeAddress(
                    cafeUuid = action.addressUuid,
                    isDelivery = dataState.isDelivery,
                )
            }

            CreateOrder.Action.DeferredTimeClick -> {
                showDeferredTime()
            }

            CreateOrder.Action.HideDeferredTime -> {
                hideDeferredTime()
            }

            CreateOrder.Action.AsapClick -> {
                changeDeferredTimeToAsap()
            }

            CreateOrder.Action.PickTimeClick -> {
                showTimePicker()
            }

            CreateOrder.Action.HideTimePicker -> {
                hideTimePicker()
            }

            is CreateOrder.Action.ChangeDeferredTime -> {
                changeDeferredTime(deferredTime = action.time)
            }

            CreateOrder.Action.PaymentMethodClick -> {
                paymentMethodClick()
            }

            CreateOrder.Action.HidePaymentMethodList -> {
                hidePaymentMethodList()
            }

            is CreateOrder.Action.ChangePaymentMethod -> {
                changePaymentMethod(paymentMethodUuid = action.paymentMethodUuid)
            }

            CreateOrder.Action.PickupServingClick -> {
                pickupServingClick()
            }

            CreateOrder.Action.HidePickupServingList -> {
                hidePickupServingList()
            }

            is CreateOrder.Action.ChangePickupServing -> {
                changePickupServing(pickupServing = action.pickupServing)
            }

            is CreateOrder.Action.ChangeWithoutChangeChecked -> {
                changeWithoutChangeChecked()
            }

            is CreateOrder.Action.ChangeChange -> {
                changeChange(change = action.change)
            }

            is CreateOrder.Action.ChangeComment -> {
                changeComment(comment = action.comment)
            }

            is CreateOrder.Action.CreateClick -> {
                createClick(
                    withoutChange = action.withoutChange,
                    changeFrom = action.changeFrom,
                    additionalUtensils = action.additionalUtensils,
                    withoutUtensils = action.withoutUtensils,
                )
            }

            CreateOrder.Action.Back ->
                addEvent {
                    CreateOrder.Event.Back
                }

            is CreateOrder.Action.ChangeAdditionalUtensils ->
                changeAdditionalUtensilsCount(
                    additionalUtensilsCount = action.additionalUtensilsCount,
                )

            CreateOrder.Action.ChangeWithoutUtensilsChecked -> {
                changeWithoutUtensilsChecked()
            }
        }
    }

    private fun loadData(isDelivery: Boolean) {
        withLoading {
            updateUserAddresses()

            updateCafeList()

            updatePaymentMethods()

            updateWithoutUtensils()

            updateCartTotal()

            updateAdditionalUtensils(isDelivery = isDelivery)
        }
    }

    private fun changeMethod(position: Int) {
        val isDelivery = position == DELIVERY_POSITION
        analyticService.sendEvent(
            event = OrderDeliveryToggleEvent(isDelivery = isDelivery),
        )
        setState {
            copy(
                isDelivery = isDelivery,
            )
        }

        updateAdditionalUtensils(isDelivery = isDelivery)

        withLoading {
            updateCartTotal()
        }
    }

    private fun updateAdditionalUtensils(isDelivery: Boolean) {
        sharedScope.launchSafe(
            block = {
                setState {
                    copy(
                        additionalUtensils =
                            getAdditionalUtensilsUseCase(
                                cafeUuid =
                                    if (isDelivery) {
                                        selectedUserAddress?.cafeUuid.orEmpty()
                                    } else {
                                        selectedCafe?.uuid.orEmpty()
                                    },
                            ),
                    )
                }
            },
            onError = {
                // stub
            },
        )
    }

    private fun userAddressClick() {
        setState {
            copy(isUserAddressListShown = true)
        }
    }

    private fun addAddressClick() {
        addEvent {
            CreateOrder.Event.OpenCreateAddressEvent
        }
    }

    private fun hideUserAddressList() {
        setState {
            copy(isUserAddressListShown = false)
        }
    }

    private fun changeUserAddress(
        userAddressUuid: String,
        isDelivery: Boolean,
    ) {
        setState {
            copy(isUserAddressListShown = false)
        }
        withLoading {
            saveSelectedUserAddress(userAddressUuid)
            updateSelectedUserAddress()
            updateAdditionalUtensils(isDelivery = isDelivery)
        }
    }

    private fun cafeAddressClick() {
        setState {
            copy(isCafeListShown = true)
        }
    }

    private fun hideCafeList() {
        setState {
            copy(isCafeListShown = false)
        }
    }

    private fun changeCafeAddress(
        cafeUuid: String,
        isDelivery: Boolean,
    ) {
        setState {
            copy(isCafeListShown = false)
        }
        withLoading {
            cafeInteractor.saveSelectedCafe(cafeUuid)
            updateCafeList()
            updateAdditionalUtensils(isDelivery = isDelivery)
        }
    }

    private fun showDeferredTime() {
        setState {
            copy(isDeferredTimeShown = true)
        }
    }

    private fun hideDeferredTime() {
        setState {
            copy(isDeferredTimeShown = false)
        }
    }

    private fun showTimePicker() {
        sharedScope.launchSafe(
            block = {
                val timeZone = getSelectedCityTimeZone()
                val minDeferredTime = getMinTime(timeZone)

                setState {
                    copy(
                        isTimePickerShown = true,
                        minDeferredTime = getMinTime(timeZone),
                        initialDeferredTime =
                            if (deferredTime is CreateOrder.DeferredTime.Later) {
                                deferredTime.time
                            } else {
                                minDeferredTime
                            },
                    )
                }
            },
            onError = {},
        )
    }

    private fun hideTimePicker() {
        setState {
            copy(isTimePickerShown = false)
        }
    }

    private fun changeDeferredTime(deferredTime: Time) {
        setState {
            copy(
                isDeferredTimeShown = false,
                isTimePickerShown = false,
                deferredTime =
                    CreateOrder.DeferredTime.Later(
                        time = deferredTime,
                    ),
                showTimePickerHint = getDeferredTimeHintUseCase(deferredTime),
                hasTimePickerError = false,
            )
        }
    }

    private fun changeDeferredTimeToAsap() {
        setState {
            copy(
                isDeferredTimeShown = false,
                deferredTime = CreateOrder.DeferredTime.Asap,
                showTimePickerHint = false,
                hasTimePickerError = false,
            )
        }
    }

    private fun paymentMethodClick() {
        setState {
            copy(
                isPaymentMethodListShown = true,
            )
        }
    }

    private fun hidePaymentMethodList() {
        setState {
            copy(
                isPaymentMethodListShown = false,
            )
        }
    }

    private fun changePaymentMethod(paymentMethodUuid: String) {
        setState {
            copy(
                isPaymentMethodListShown = false,
            )
        }
        withLoading {
            savePaymentMethodUseCase(paymentMethodUuid)
            updatePaymentMethods()
        }
    }

    private fun pickupServingClick() {
        setState {
            copy(
                isPickupServingListShown = true,
            )
        }
    }

    private fun hidePickupServingList() {
        setState {
            copy(
                isPickupServingListShown = false,
            )
        }
    }

    private fun changePickupServing(pickupServing: PickupServing) {
        setState {
            copy(
                selectedPickupServing = pickupServing,
                isPickupServingListShown = false,
            )
        }
    }

    private fun changeWithoutChangeChecked() {
        setState {
            copy(withoutChangeChecked = !withoutChangeChecked)
        }
    }

    private fun changeChange(change: String) {
        setState {
            copy(change = change.toIntOrNull())
        }
    }

    private fun changeComment(comment: String) {
        setState {
            copy(comment = comment)
        }
    }

    private fun createClick(
        withoutChange: String,
        changeFrom: String,
        additionalUtensils: String,
        withoutUtensils: String,
    ) {
        val state = mutableDataState.value

        val isDeliveryAddressNotSelected =
            state.isDelivery && (state.selectedUserAddress == null)

        if (isDeliveryAddressNotSelected) {
            setState {
                copy(isAddressErrorShown = CreateOrder.DataState.AddressErrorState.ERROR)
            }
            addEvent {
                CreateOrder.Event.ShowUserAddressError
            }
            return
        }

        val isPaymentMethodNotSelected = state.selectedPaymentMethod == null
        setState {
            copy(isPaymentMethodErrorShown = isPaymentMethodNotSelected)
        }
        if (isPaymentMethodNotSelected) {
            addEvent {
                CreateOrder.Event.ShowPaymentMethodError
            }
            return
        }

        val newFinalCost =
            (state.cartTotal as? CreateOrder.CartTotal.Success)?.newFinalCostValue ?: 0
        val isChangeLessThenCost = (state.change ?: 0) < newFinalCost
        val isChangeIncorrect =
            state.paymentByCash &&
                !state.withoutChangeChecked &&
                isChangeLessThenCost
        setState {
            copy(isChangeErrorShown = isChangeIncorrect)
        }
        if (isChangeIncorrect) {
            addEvent {
                CreateOrder.Event.ShowChangeError
            }
            return
        }

        val isAdditionalUtensilsIncorrect =
            state.additionalUtensils &&
                !state.withoutUtensilsChecked &&
                state.additionalUtensilsCount.isEmpty()

        setState {
            copy(isAdditionalUtensilsErrorShown = isAdditionalUtensilsIncorrect)
        }

        if (isAdditionalUtensilsIncorrect) {
            addEvent {
                CreateOrder.Event.ShowAdditionalUtensilsError
            }
            return
        }

        if (state.hasTimePickerError) {
            addEvent {
                CreateOrder.Event.ShowTimePickerError
            }
            return
        }

        withLoading(
            onOrderCreateError = { throwable -> sendOrderCreateFailEvent(throwable) },
        ) {
            if (userInteractor.isUserAuthorize()) {
                analyticService.sendEvent(
                    event =
                        OrderCreateClickEvent(
                            isDelivery = state.isDelivery,
                            paymentMethod =
                                state.selectedPaymentMethod
                                    ?.name
                                    ?.name
                                    .orEmpty(),
                        ),
                )
                val orderCode =
                    createOrder(
                        isDelivery = state.isDelivery,
                        selectedUserAddress = state.selectedUserAddress,
                        selectedCafe = state.selectedCafe,
                        orderComment =
                            getExtendedCommentUseCase(
                                ExtendedComment(
                                    comment = state.comment,
                                    change =
                                        ExtendedComment.Change(
                                            paymentByCash = state.paymentByCash,
                                            withoutChangeChecked = state.withoutChangeChecked,
                                            withoutChange = withoutChange,
                                            changeFrom = changeFrom,
                                            change = state.change?.toString().orEmpty(),
                                        ),
                                    additionalUtensils =
                                        ExtendedComment.AdditionalUtensils(
                                            isAdditionalUtensils = state.additionalUtensils,
                                            withoutUtensilsChecked = state.withoutUtensilsChecked,
                                            withoutUtensils = withoutUtensils,
                                            count = state.additionalUtensilsCount,
                                            name = additionalUtensils,
                                        ),
                                ),
                            ).takeIf { comment ->
                                comment.isNotBlank()
                            },
                        deferredTime =
                            when (state.deferredTime) {
                                CreateOrder.DeferredTime.Asap -> null
                                is CreateOrder.DeferredTime.Later -> state.deferredTime.time
                            },
                        timeZone = getSelectedCityTimeZone(),
                        paymentMethod = state.selectedPaymentMethod.name.name,
                        pickupServing = if (state.isDelivery) null else state.selectedPickupServing,
                    )
                if (orderCode == null) {
                    sendOrderCreateFailEvent(error = "null_order")
                    addEvent {
                        CreateOrder.Event.ShowSomethingWentWrongErrorEvent
                    }
                } else {
                    analyticService.sendEvent(event = OrderCreateSuccessEvent())
                    cartProductInteractor.removeAllProductsFromCart()
                    addEvent {
                        CreateOrder.Event.OrderCreatedEvent(code = orderCode.code)
                    }
                }
            } else {
                sendOrderCreateFailEvent(error = "unauthorized")
                addEvent {
                    CreateOrder.Event.ShowUserUnauthorizedErrorEvent
                }
            }
        }
    }

    private fun sendOrderCreateFailEvent(error: String) {
        analyticService.sendEvent(event = OrderCreateFailEvent(error = error))
    }

    private fun sendOrderCreateFailEvent(throwable: Throwable) {
        val error =
            when (throwable) {
                is OrderNotAvailableException -> "not_available"
                is NotAllowedTimeForOrderException -> "time_error"
                else -> "unknown"
            }
        sendOrderCreateFailEvent(error = error)
    }

    private suspend fun updateUserAddresses() {
        updateSelectedUserAddress()
    }

    private suspend fun updatePaymentMethods() {
        val paymentMethodList = getSelectablePaymentMethodListUseCase()

        val selectedPaymentMethod =
            getSelectedPaymentMethodUseCase(selectablePaymentMethodList = paymentMethodList)

        setState {
            copy(
                paymentMethodList = paymentMethodList,
                selectedPaymentMethod = selectedPaymentMethod,
            )
        }
        if (selectedPaymentMethod != null) {
            setState {
                copy(isPaymentMethodErrorShown = false)
            }
        }
    }

    private suspend fun updateSelectedUserAddress() {
        val userAddress = getCurrentUserAddressUseCase()
        val userAddressList = getSelectableUserAddressList()

        setState {
            copy(
                userAddressList = userAddressList,
                selectedUserAddress = userAddress,
                deliveryState =
                    userAddress?.cafeUuid?.let { cafeUuid ->
                        if (isDeliveryEnabledFromCafeUseCase(cafeUuid = cafeUuid)) {
                            CreateOrder.DataState.DeliveryState.ENABLED
                        } else {
                            CreateOrder.DataState.DeliveryState.NOT_ENABLED
                        }
                    } ?: CreateOrder.DataState.DeliveryState.NEED_ADDRESS,
                isAddressErrorShown =
                    if (userAddress != null) {
                        CreateOrder.DataState.AddressErrorState.NO_ERROR
                    } else {
                        isAddressErrorShown
                    },
                workload =
                    userAddress?.cafeUuid?.let { cafeUuid ->
                        getWorkloadCafeUseCase(cafeUuid = cafeUuid)
                    } ?: Cafe.Workload.LOW,
                isLoadingSwitcher = false,
            )
        }
    }

    private suspend fun updateCafeList() {
        val cafeList = getSelectableCafeList()
        setState {
            copy(cafeList = cafeList)
        }

        cafeList
            .find { cafe ->
                cafe.isSelected
            }?.let { selectedCafe ->
                setState {
                    copy(
                        selectedCafe = selectedCafe.cafe,
                        isPickupEnabled =
                            isPickupEnabledFromCafeUseCase(
                                cafeUuid = selectedCafe.cafe.uuid,
                            ),
                        hasOpenedCafe =
                            hasOpenedCafeUseCase(
                                cafeList =
                                    cafeList.map { selectableCafe ->
                                        selectableCafe.cafe
                                    },
                            ),
                    )
                }
            }
    }

    private suspend fun updateCartTotal() {
        getCartTotalJob?.cancel()
        getCartTotalJob =
            sharedScope.launchSafe(
                block = {
                    val isDelivery = mutableDataState.value.isDelivery

                    getCartTotalFlowUseCase(isDelivery = isDelivery).collect { cartTotal ->

                        val motivation =
                            getMotivationUseCase(
                                newTotalCost = cartTotal.newTotalCost,
                                isDelivery = isDelivery,
                            )
                        val motivationData = motivation?.toMotivationData()

                        setState {
                            copy(
                                cartTotal =
                                    CreateOrder.CartTotal.Success(
                                        motivation = motivationData,
                                        discount =
                                            cartTotal.discount?.let { discount ->
                                                "$discount$PERCENT"
                                            },
                                        deliveryCost =
                                            cartTotal.deliveryCost?.let { deliveryCost ->
                                                "$deliveryCost $RUBLE_CURRENCY"
                                            },
                                        oldFinalCost =
                                            cartTotal.oldFinalCost?.let { oldFinalCost ->
                                                "$oldFinalCost $RUBLE_CURRENCY"
                                            },
                                        newFinalCost = "${cartTotal.newFinalCost} $RUBLE_CURRENCY",
                                        newFinalCostValue = cartTotal.newFinalCost,
                                    ),
                                isDelivery = isDelivery,
                            )
                        }
                    }
                },
                onError = { error ->
                    Logger.logE(CREATION_ORDER_VIEW_MODEL_TAG, error.stackTraceToString())
                },
            )
    }

    fun changeAdditionalUtensilsCount(additionalUtensilsCount: String) {
        setState {
            copy(
                additionalUtensilsCount = additionalUtensilsCount,
            )
        }
    }

    private suspend fun updateWithoutUtensils() {
        setState {
            copy(withoutUtensilsChecked = getWithoutUtensilsUseCase())
        }
    }

    private fun changeWithoutUtensilsChecked() {
        val withoutUtensilsChecked = !mutableDataState.value.withoutUtensilsChecked
        setState {
            copy(
                withoutUtensilsChecked = withoutUtensilsChecked,
                isAdditionalUtensilsErrorShown = false,
            )
        }
        sharedScope.launchSafe(
            block = {
                saveWithoutUtensilsUseCase(withoutUtensils = withoutUtensilsChecked)
            },
            onError = {
                addEvent {
                    CreateOrder.Event.ShowSomethingWentWrongErrorEvent
                }
            },
        )
    }

    private inline fun withLoading(
        noinline onOrderCreateError: ((Throwable) -> Unit)? = null,
        crossinline block: suspend () -> Unit,
    ) {
        sharedScope.launchSafe(
            {
                setState {
                    copy(isLoading = true)
                }

                block()
                setState {
                    copy(isLoading = false)
                }
            },
            onError = { throwable ->
                onOrderCreateError?.invoke(throwable)
                when (throwable) {
                    is OrderNotAvailableException -> {
                        setState {
                            copy(
                                deliveryState = CreateOrder.DataState.DeliveryState.NOT_ENABLED,
                            )
                        }
                        addEvent {
                            CreateOrder.Event.OrderNotAvailableErrorEvent
                        }
                    }

                    is NotAllowedTimeForOrderException -> {
                        setState {
                            copy(
                                hasTimePickerError = true,
                                showTimePickerHint = true,
                            )
                        }
                        addEvent {
                            CreateOrder.Event.ShowTimePickerError
                        }
                    }

                    else ->
                        addEvent {
                            CreateOrder.Event.ShowSomethingWentWrongErrorEvent
                        }
                }
                setState {
                    copy(isLoading = false)
                }
            },
        )
    }
}
