package com.bunbeauty.shared.data

import com.bunbeauty.core.model.Discount
import com.bunbeauty.core.model.Settings
import com.bunbeauty.core.model.UserCityUuid
import com.bunbeauty.shared.DataStoreRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import org.koin.core.component.KoinComponent
import platform.Foundation.NSUserDefaults

actual class DataStoreRepository :
    DataStoreRepo,
    KoinComponent {
    private val tokenState =
        MutableStateFlow(
            NSUserDefaults.standardUserDefaults.stringForKey(TOKEN_KEY),
        )

    actual override val token: Flow<String?> = tokenState.asStateFlow()

    actual override suspend fun getToken(): String? = tokenState.value

    actual override suspend fun saveToken(token: String) {
        NSUserDefaults.standardUserDefaults.setObject(token, TOKEN_KEY)
        tokenState.value = token
    }

    actual override suspend fun clearToken() {
        NSUserDefaults.standardUserDefaults.removeObjectForKey(TOKEN_KEY)
        tokenState.value = null
    }

    actual override val userUuid: Flow<String?> =
        flow {
            emit(NSUserDefaults.standardUserDefaults.stringForKey(USER_UUID_KEY))
        }

    actual override suspend fun getUserUuid(): String? = userUuid.firstOrNull()

    actual override suspend fun saveUserUuid(userId: String) {
        NSUserDefaults.standardUserDefaults.setObject(userId, USER_UUID_KEY)
    }

    actual override suspend fun clearUserUuid() {
        NSUserDefaults.standardUserDefaults.removeObjectForKey(USER_UUID_KEY)
    }

    actual override val selectedCityUuid: Flow<String?> =
        flow {
            emit(NSUserDefaults.standardUserDefaults.stringForKey(SELECTED_CITY_UUID_KEY))
        }

    actual override suspend fun saveSelectedCityUuid(cityUuid: String) {
        NSUserDefaults.standardUserDefaults.setObject(cityUuid, SELECTED_CITY_UUID_KEY)
    }

    actual override suspend fun getSelectedCityUuid(): String? = NSUserDefaults.standardUserDefaults.stringForKey(SELECTED_CITY_UUID_KEY)

    actual override val selectedPaymentMethodUuid: Flow<String?> =
        flow {
            emit(NSUserDefaults.standardUserDefaults.stringForKey(SELECTED_PAYMENT_METHOD_UUID_KEY))
        }

    actual override suspend fun saveSelectedPaymentMethodUuid(selectedPaymentMethodUuid: String) {
        NSUserDefaults.standardUserDefaults.setObject(
            selectedPaymentMethodUuid,
            SELECTED_PAYMENT_METHOD_UUID_KEY,
        )
    }

    actual override val withoutUtensils: Flow<Boolean?> =
        flow {
            emit(getWithoutUtensils())
        }

    actual override suspend fun getWithoutUtensils(): Boolean? = NSUserDefaults.standardUserDefaults.boolForKey(WITHOUT_UTENSILS_KEY)

    actual override suspend fun saveWithoutUtensils(withoutUtensils: Boolean) {
        NSUserDefaults.standardUserDefaults.setBool(
            withoutUtensils,
            WITHOUT_UTENSILS_KEY,
        )
    }

    actual override suspend fun clearWithoutUtensils() {
        NSUserDefaults.standardUserDefaults.removeObjectForKey(WITHOUT_UTENSILS_KEY)
    }

    actual override val discount: Flow<Discount?> =
        flow {
            emit(
                Discount(
                    firstOrderDiscount =
                        NSUserDefaults.standardUserDefaults
                            .integerForKey(FIRST_DISCOUNT_KEY)
                            .toInt(),
                ),
            )
        }

    actual override suspend fun getDiscount(): Discount? = discount.firstOrNull()

    actual override suspend fun saveDiscount(discount: Discount) {
        NSUserDefaults.standardUserDefaults.setObject(
            discount.firstOrderDiscount,
            FIRST_DISCOUNT_KEY,
        )
    }

    actual override fun observeUserAndCityUuid(): Flow<UserCityUuid> =
        flow {
            emit(
                UserCityUuid(
                    userUuid =
                        NSUserDefaults.standardUserDefaults
                            .stringForKey(
                                USER_UUID_KEY,
                            ).toString(),
                    cityUuid =
                        NSUserDefaults.standardUserDefaults
                            .stringForKey(
                                SELECTED_CITY_UUID_KEY,
                            ).toString(),
                ),
            )
        }

    actual override suspend fun getUserAndCityUuid(): UserCityUuid =
        UserCityUuid(
            userUuid = getUserUuid() ?: "",
            cityUuid = getSelectedCityUuid() ?: "",
        )

    actual override val settings: Flow<Settings?> =
        flow {
            emit(
                Settings(
                    userUuid =
                        NSUserDefaults.standardUserDefaults
                            .stringForKey(
                                USER_UUID_KEY,
                            ).toString(),
                    phoneNumber =
                        NSUserDefaults.standardUserDefaults
                            .stringForKey(
                                SETTINGS_PHONE_NUMBER_KEY,
                            ).toString(),
                    email =
                        NSUserDefaults.standardUserDefaults
                            .stringForKey(
                                SETTINGS_EMAIL_KEY,
                            ).toString(),
                    personalDiscountPercent =
                        NSUserDefaults.standardUserDefaults
                            .objectForKey(SETTINGS_PERSONAL_DISCOUNT_PERCENT_KEY)
                            ?.let {
                                NSUserDefaults.standardUserDefaults
                                    .integerForKey(SETTINGS_PERSONAL_DISCOUNT_PERCENT_KEY)
                                    .toInt()
                            },
                    marketingNotificationsEnabled =
                        NSUserDefaults.standardUserDefaults
                            .objectForKey(SETTINGS_MARKETING_NOTIFICATIONS_KEY)
                            ?.let {
                                NSUserDefaults.standardUserDefaults
                                    .boolForKey(SETTINGS_MARKETING_NOTIFICATIONS_KEY)
                            } ?: true,
                    systemNotificationsEnabled =
                        NSUserDefaults.standardUserDefaults
                            .boolForKey(SETTINGS_SYSTEM_NOTIFICATIONS_KEY),
                ),
            )
        }

    actual override suspend fun getSettings(): Settings? = settings.firstOrNull()

    actual override suspend fun saveSettings(settings: Settings) {
        NSUserDefaults.standardUserDefaults.setObject(settings.userUuid, SETTINGS_USER_UUID_KEY)
        NSUserDefaults.standardUserDefaults.setObject(
            settings.phoneNumber,
            SETTINGS_PHONE_NUMBER_KEY,
        )
        NSUserDefaults.standardUserDefaults.setObject(settings.email, SETTINGS_EMAIL_KEY)
        NSUserDefaults.standardUserDefaults.setBool(
            settings.marketingNotificationsEnabled,
            SETTINGS_MARKETING_NOTIFICATIONS_KEY,
        )
        NSUserDefaults.standardUserDefaults.setBool(
            settings.systemNotificationsEnabled,
            SETTINGS_SYSTEM_NOTIFICATIONS_KEY,
        )
        val personalDiscountPercent = settings.personalDiscountPercent
        if (personalDiscountPercent != null) {
            NSUserDefaults.standardUserDefaults.setObject(
                personalDiscountPercent,
                SETTINGS_PERSONAL_DISCOUNT_PERCENT_KEY,
            )
        } else {
            NSUserDefaults.standardUserDefaults.removeObjectForKey(
                SETTINGS_PERSONAL_DISCOUNT_PERCENT_KEY,
            )
        }
    }

    actual override val recommendationMaxVisible: Flow<Int?> =
        flow {
            emit(
                NSUserDefaults.standardUserDefaults
                    .integerForKey(
                        RECOMMENDATION_MAX_VISIBLE_KEY,
                    ).toInt(),
            )
        }

    actual override suspend fun getRecommendationMaxVisible(): Int? = recommendationMaxVisible.firstOrNull()

    actual override suspend fun saveRecommendationMaxVisible(recommendationMaxVisible: Int) {
        NSUserDefaults.standardUserDefaults.setObject(
            recommendationMaxVisible,
            RECOMMENDATION_MAX_VISIBLE_KEY,
        )
    }

    actual override val userCafeUuid: Flow<String?> =
        flow {
            emit(
                NSUserDefaults.standardUserDefaults.stringForKey(
                    USER_CAFE_UUID_KEY,
                ),
            )
        }

    actual override suspend fun getUserCafeUuid(): String? = userCafeUuid.firstOrNull()

    actual override suspend fun saveUserCafeUuid(userCafeUuid: String) {
        NSUserDefaults.standardUserDefaults.setObject(
            userCafeUuid,
            USER_CAFE_UUID_KEY,
        )
    }

    actual override suspend fun clearUserData() {
        NSUserDefaults.standardUserDefaults.removeObjectForKey(TOKEN_KEY)
        tokenState.value = null
        NSUserDefaults.standardUserDefaults.removeObjectForKey(USER_UUID_KEY)
        clearWithoutUtensils()
        removeUserSettings()
    }

    private fun removeUserSettings() {
        NSUserDefaults.standardUserDefaults.removeObjectForKey(SETTINGS_USER_UUID_KEY)
        NSUserDefaults.standardUserDefaults.removeObjectForKey(SETTINGS_PHONE_NUMBER_KEY)
        NSUserDefaults.standardUserDefaults.removeObjectForKey(SETTINGS_EMAIL_KEY)
        NSUserDefaults.standardUserDefaults.removeObjectForKey(SETTINGS_PERSONAL_DISCOUNT_PERCENT_KEY)
        NSUserDefaults.standardUserDefaults.removeObjectForKey(SETTINGS_MARKETING_NOTIFICATIONS_KEY)
        NSUserDefaults.standardUserDefaults.removeObjectForKey(SETTINGS_SYSTEM_NOTIFICATIONS_KEY)
    }

    companion object {
        const val TOKEN_KEY = "TOKEN_KEY"
        const val SELECTED_CITY_UUID_KEY = "SELECTED_CITY_UUID_KEY"
        const val SELECTED_CITY_TIME_ZONE_KEY = "SELECTED_CITY_TIME_ZONE_KEY"
        const val USER_UUID_KEY = "USER_UUID_KEY"
        private const val SETTINGS_USER_UUID_KEY = "SETTINGS_USER_UUID_KEY"
        private const val SETTINGS_PHONE_NUMBER_KEY = "SETTINGS_PHONE_NUMBER_KEY"
        private const val SETTINGS_EMAIL_KEY = "SETTINGS_EMAIL_KEY"
        private const val SETTINGS_PERSONAL_DISCOUNT_PERCENT_KEY =
            "SETTINGS_PERSONAL_DISCOUNT_PERCENT_KEY"
        private const val SETTINGS_MARKETING_NOTIFICATIONS_KEY = "SETTINGS_MARKETING_NOTIFICATIONS_KEY"
        private const val SETTINGS_SYSTEM_NOTIFICATIONS_KEY = "SETTINGS_SYSTEM_NOTIFICATIONS_KEY"
        private const val SELECTED_PAYMENT_METHOD_UUID_KEY = "SELECTED_PAYMENT_METHOD_UUID_KEY"
        private const val WITHOUT_UTENSILS_KEY = "WITHOUT_UTENSILS_KEY"
        private const val FIRST_DISCOUNT_KEY = "FIRST_DISCOUNT_KEY"
        private const val RECOMMENDATION_MAX_VISIBLE_KEY = "RECOMMENDATION_MAX_VISIBLE_KEY"
        private const val USER_CAFE_UUID_KEY = "USER_CAFE_UUID_KEY"
    }
}
