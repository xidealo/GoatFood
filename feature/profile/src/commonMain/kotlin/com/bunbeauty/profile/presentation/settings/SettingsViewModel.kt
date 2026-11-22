package com.bunbeauty.profile.presentation.settings

import com.bunbeauty.analytic.AnalyticService
import com.bunbeauty.analytic.event.LogoutSettingsClickEvent
import com.bunbeauty.core.base.SharedStateViewModel
import com.bunbeauty.core.domain.DisableUserUseCase
import com.bunbeauty.core.domain.city.GetCityListUseCase
import com.bunbeauty.core.domain.city.ObserveSelectedCityUseCase
import com.bunbeauty.core.domain.city.SaveSelectedCityUseCase
import com.bunbeauty.core.domain.repo.SettingsRepo
import com.bunbeauty.core.domain.settings.ObserveSettingsUseCase
import com.bunbeauty.core.domain.settings.UpdateEmailUseCase
import com.bunbeauty.core.model.Settings
import com.bunbeauty.core.domain.user.IUserInteractor
import com.bunbeauty.core.domain.notification.NotificationSettingsHandler
import com.bunbeauty.core.extension.launchSafe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val observeSettingsUseCase: ObserveSettingsUseCase,
    private val observeSelectedCityUseCase: ObserveSelectedCityUseCase,
    private val getCityListUseCase: GetCityListUseCase,
    private val saveSelectedCityUseCase: SaveSelectedCityUseCase,
    private val disableUserUseCase: DisableUserUseCase,
    private val userInteractor: IUserInteractor,
    private val analyticService: AnalyticService,
    private val settingsRepository: SettingsRepo,
    private val notificationSettingsHandler: NotificationSettingsHandler,
    private val updateEmailUseCase: UpdateEmailUseCase,
) : SharedStateViewModel<SettingsState.DataState, SettingsState.Action, SettingsState.Event>(
        SettingsState.DataState(),
    ) {
    private var observeSettingsJob: Job? = null
    private var marketingNotificationsUpdateId = 0L

    private companion object {
        val EMAIL_REGEX = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
    }

    override fun reduce(
        action: SettingsState.Action,
        dataState: SettingsState.DataState,
    ) {
        when (action) {
            SettingsState.Action.BackClick -> backClick()
            SettingsState.Action.LoadData -> loadData()
            SettingsState.Action.OnCityClicked -> onCityClicked()

            SettingsState.Action.OnLogoutClicked -> onLogoutClicked()

            SettingsState.Action.OnLogoutConfirmClicked -> logout()

            SettingsState.Action.CloseLogoutBottomSheet -> onCloseLogoutClicked()
            is SettingsState.Action.OnCitySelected -> onCitySelected(action.cityUuid)
            SettingsState.Action.CloseCityListBottomSheet -> onCloseCityListBottomSheetClicked()
            SettingsState.Action.DisableUser -> onDisableUserClicked()
            SettingsState.Action.CloseDisableUserBottomSheet -> onCloseDisableUserClickedBS()
            SettingsState.Action.DisableUserBottomSheet -> disableUser()
            SettingsState.Action.OnEmailClicked -> onEmailClicked()
            SettingsState.Action.CloseEmailBottomSheet -> onCloseEmailBottomSheet()
            is SettingsState.Action.OnEmailChanged -> onEmailChanged(action.value)
            SettingsState.Action.SaveEmail -> saveEmail()
            is SettingsState.Action.OnMarketingNotificationsChanged ->
                updateMarketingNotifications(action.enabled)
        }
    }

    fun loadData() {
        observeSettings()
        loadCityList()
    }

    private fun backClick() {
        addEvent {
            SettingsState.Event.Back
        }
    }

    private fun onLogoutClicked() {
        analyticService.sendEvent(event = LogoutSettingsClickEvent())

        setState {
            copy(
                isShowLogoutBottomSheet = true,
            )
        }
    }

    private fun onCloseLogoutClicked() {
        setState {
            copy(
                isShowLogoutBottomSheet = false,
            )
        }
    }

    private fun onCloseCityListBottomSheetClicked() {
        setState {
            copy(
                isShowCityListBottomSheet = false,
            )
        }
    }

    private fun onCityClicked() {
        setState {
            copy(
                isShowCityListBottomSheet = true,
            )
        }
    }

    fun onCitySelected(cityUuid: String) {
        sharedScope.launch {
            saveSelectedCityUseCase(cityUuid)
            onCloseCityListBottomSheetClicked()
        }
    }

    fun logout() {
        sharedScope.launchSafe(
            block = {
                setState {
                    copy(
                        isShowLogoutBottomSheet = false,
                        state = SettingsState.DataState.State.LOADING,
                    )
                }
                observeSettingsJob?.cancel()
                notificationSettingsHandler.setNewsSubscription(enabled = false)
                userInteractor.clearUserCache()
                addEvent {
                    SettingsState.Event.Back
                }
            },
            onError = { error ->
                println(error)
            },
        )
    }

    fun onDisableUserClicked() {
        setState {
            copy(
                isShowDisableUserBottomSheet = true,
            )
        }
    }

    fun onCloseDisableUserClickedBS() {
        setState {
            copy(
                isShowDisableUserBottomSheet = false,
            )
        }
    }

    fun disableUser() {
        sharedScope.launch {
            setState {
                copy(
                    state = SettingsState.DataState.State.LOADING,
                )
            }
            disableUserUseCase()
            logout()
        }
    }

    private fun onEmailClicked() {
        setState {
            copy(
                isShowEmailBottomSheet = true,
                emailInput = dataState.value.settings?.email.orEmpty(),
            )
        }
    }

    private fun onCloseEmailBottomSheet() {
        setState {
            copy(isShowEmailBottomSheet = false)
        }
    }

    private fun onEmailChanged(value: String) {
        setState {
            copy(emailInput = value)
        }
    }

    private fun saveEmail() {
        val email = dataState.value.emailInput.trim()
        if (email.isNotEmpty() && !EMAIL_REGEX.matches(email)) {
            addEvent { SettingsState.Event.ShowEmailChangingFailedEvent }
            return
        }
        sharedScope.launchSafe(
            block = {
                val success = updateEmailUseCase(email.ifBlank { null })
                if (success) {
                    val currentSettings = dataState.value.settings
                    setState {
                        copy(
                            settings = currentSettings?.withEmail(email),
                            isShowEmailBottomSheet = false,
                        )
                    }
                    addEvent { SettingsState.Event.ShowEmailChangedSuccessfullyEvent }
                } else {
                    addEvent { SettingsState.Event.ShowEmailChangingFailedEvent }
                }
            },
            onError = {
                addEvent { SettingsState.Event.ShowEmailChangingFailedEvent }
            },
        )
    }

    private fun Settings.withEmail(email: String): Settings {
        return Settings(
            userUuid = userUuid,
            phoneNumber = phoneNumber,
            email = email.ifBlank { null },
            personalDiscountPercent = personalDiscountPercent,
            marketingNotificationsEnabled = marketingNotificationsEnabled,
            systemNotificationsEnabled = systemNotificationsEnabled,
        )
    }

    private fun Settings.withMarketingNotifications(enabled: Boolean): Settings {
        return Settings(
            userUuid = userUuid,
            phoneNumber = phoneNumber,
            email = email,
            personalDiscountPercent = personalDiscountPercent,
            marketingNotificationsEnabled = enabled,
            systemNotificationsEnabled = systemNotificationsEnabled,
        )
    }

    private fun updateMarketingNotifications(enabled: Boolean) {
        val updateId = ++marketingNotificationsUpdateId
        val previousEnabled = dataState.value.settings?.marketingNotificationsEnabled ?: true
        setState {
            copy(settings = settings?.withMarketingNotifications(enabled))
        }
        sharedScope.launchSafe(
            block = {
                val settings = settingsRepository.updateMarketingNotifications(enabled)
                if (updateId == marketingNotificationsUpdateId) {
                    if (settings == null) {
                        rollbackMarketingNotifications(previousEnabled)
                    } else {
                        setState { copy(settings = settings) }
                        notificationSettingsHandler.setNewsSubscription(settings.marketingNotificationsEnabled)
                        if (enabled) {
                            notificationSettingsHandler.requestPermission()
                        }
                    }
                }
            },
            onError = {
                if (updateId == marketingNotificationsUpdateId) {
                    rollbackMarketingNotifications(previousEnabled)
                }
            },
        )
    }

    private fun rollbackMarketingNotifications(enabled: Boolean) {
        setState {
            copy(settings = settings?.withMarketingNotifications(enabled))
        }
        addEvent { SettingsState.Event.ShowNotificationSettingsFailedEvent }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeSettings() {
        observeSettingsJob?.cancel()
        observeSettingsJob =
            observeSelectedCityUseCase()
                .flatMapLatest { city ->
                    observeSettingsUseCase().map { settings ->
                        setState {
                            val state =
                                if (city == null || settings == null) {
                                    SettingsState.DataState.State.ERROR
                                } else {
                                    SettingsState.DataState.State.SUCCESS
                                }

                            copy(
                                settings = settings,
                                selectedCity = city,
                                state = state,
                            )
                        }
                    }
                }.launchIn(sharedScope)
    }

    private fun loadCityList() {
        sharedScope.launchSafe(
            block = {
                setState {
                    copy(cityList = getCityListUseCase())
                }
            },
            onError = { error ->
                println(error)
            },
        )
    }
}
