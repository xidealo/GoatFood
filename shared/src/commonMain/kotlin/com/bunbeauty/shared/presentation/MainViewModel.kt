package com.bunbeauty.shared.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bunbeauty.analytic.AnalyticService
import com.bunbeauty.analytic.event.notification.NotificationOpenEvent
import com.bunbeauty.core.Logger
import com.bunbeauty.core.domain.repo.UserRepo
import com.bunbeauty.core.domain.orderavailable.IsOrderAvailableUseCase
import com.bunbeauty.core.extension.launchSafe
import com.bunbeauty.shared.NetworkUtil
import com.bunbeauty.shared.domain.feature.notification.NotificationOpen
import com.bunbeauty.shared.ui.screen.main.FoodDeliveryMessage
import com.bunbeauty.shared.ui.screen.main.FoodDeliveryMessageType
import com.bunbeauty.shared.ui.screen.main.MainState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

private const val MAIN_VIEW_MODEL_TAG = "MainViewModel"

class MainViewModel(
    private val networkUtil: NetworkUtil,
    private val isOrderAvailableUseCase: IsOrderAvailableUseCase,
    private val userRepository: UserRepo,
    private val analyticService: AnalyticService,
) : ViewModel() {
    private val mutableMainState: MutableStateFlow<MainState> = MutableStateFlow(MainState())
    val mainState: StateFlow<MainState> = mutableMainState.asStateFlow()

    init {
        observeNetworkConnection()
        checkStatusBarMessage()
    }

    fun showInfoMessage(
        text: String,
        paddingBottom: Int,
    ) {
        showMessage(
            text = text,
            type = FoodDeliveryMessageType.INFO,
            paddingBottom = paddingBottom,
        )
    }

    fun showErrorMessage(text: String) {
        showMessage(
            text = text,
            type = FoodDeliveryMessageType.ERROR,
            paddingBottom = 0,
        )
    }

    suspend fun isAuthorized(): Boolean = userRepository.getToken() != null

    fun onNotificationOpened(notification: NotificationOpen) {
        analyticService.sendEvent(
            NotificationOpenEvent(
                campaign = notification.campaign,
                touch = notification.touch,
                variant = notification.variant,
            ),
        )
        viewModelScope.launchSafe(
            block = { userRepository.markNotificationOpened(notification.notificationId) },
            onError = { error -> Logger.logE(MAIN_VIEW_MODEL_TAG, error.stackTraceToString()) },
        )
    }

    fun consumeEventList(eventList: List<MainState.Event>) {
        mutableMainState.update { state ->
            state - eventList
        }
    }

    private fun showMessage(
        text: String,
        type: FoodDeliveryMessageType,
        paddingBottom: Int,
    ) {
        mutableMainState.update { state ->
            state.copy(
                paddingBottomSnackbar = paddingBottom,
            ) +
                MainState.Event.ShowMessageEvent(
                    message =
                        FoodDeliveryMessage(
                            type = type,
                            text = text,
                        ),
                )
        }
    }

    private fun observeNetworkConnection() {
        networkUtil
            .observeIsOnline()
            .onEach { isOnline ->
                mutableMainState.update { state ->
                    state.copy(connectionLost = !isOnline)
                }
            }.launchIn(viewModelScope)
    }

    private fun checkStatusBarMessage() {
        viewModelScope.launchSafe(
            block = {
                mutableMainState.update { state ->
                    state.copy(
                        statusBarMessage =
                            MainState.StatusBarMessage(
                                isVisible = !isOrderAvailableUseCase(),
                            ),
                    )
                }
            },
            onError = { error ->
                Logger.logE(MAIN_VIEW_MODEL_TAG, error.stackTraceToString())
            },
        )
    }
}
