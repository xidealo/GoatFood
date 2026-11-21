package com.bunbeauty.shared.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.bunbeauty.core.model.Discount
import com.bunbeauty.core.model.Settings
import com.bunbeauty.core.model.UserCityUuid
import com.bunbeauty.shared.DataStoreRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class DataStoreRepository :
    DataStoreRepo,
    KoinComponent {
    private val context: Context by inject()

    private val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(
        name = TOKEN_DATA_STORE,
    )

    private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
        name = SETTINGS_DATA_STORE,
    )

    private val Context.userUuidDataStore: DataStore<Preferences> by preferencesDataStore(
        name = USER_UUID_DATA_STORE,
    )

    private val Context.selectedCityDataStore: DataStore<Preferences> by preferencesDataStore(
        name = SELECTED_CITY_DATA_STORE,
    )

    private val Context.selectedPaymentMethodDataStore: DataStore<Preferences> by preferencesDataStore(
        name = SELECTED_PAYMENT_METHOD_DATA_STORE,
    )

    private val Context.withoutUtensilsDataStore: DataStore<Preferences> by preferencesDataStore(
        name = WITHOUT_UTENSILS_DATA_STORE,
    )

    private val Context.discountDataStore: DataStore<Preferences> by preferencesDataStore(
        name = DISCOUNT_DATA_STORE,
    )

    private val Context.recommendationDataStore: DataStore<Preferences> by preferencesDataStore(
        name = RECOMMENDATION_DATA_STORE,
    )

    private val Context.userCafeUuid: DataStore<Preferences> by preferencesDataStore(
        name = USER_CAFE_DATA_STORE,
    )

    actual override val token: Flow<String?> =
        context.tokenDataStore.data.map { tokenDataStore ->
            tokenDataStore[TOKEN_KEY]
        }

    actual override suspend fun getToken() = token.firstOrNull()

    actual override suspend fun saveToken(token: String) {
        context.tokenDataStore.edit { tokenDataStore ->
            tokenDataStore[TOKEN_KEY] = token
        }
    }

    actual override suspend fun clearToken() {
        context.tokenDataStore.edit { tokenDataStore ->
            tokenDataStore.clear()
        }
    }

    actual override val settings: Flow<Settings?> =
        context.settingsDataStore.data.map { settingsDataStore ->
            settingsDataStore[SETTINGS_USER_UUID_KEY]?.let { userUuid ->
                settingsDataStore[SETTINGS_PHONE_NUMBER_KEY]?.let { phoneNumber ->
                    Settings(
                        userUuid = userUuid,
                        phoneNumber = phoneNumber,
                        email = settingsDataStore[SETTINGS_EMAIL_KEY],
                        personalDiscountPercent =
                            settingsDataStore[SETTINGS_PERSONAL_DISCOUNT_PERCENT_KEY],
                        marketingNotificationsEnabled =
                            settingsDataStore[SETTINGS_MARKETING_NOTIFICATIONS_KEY] ?: true,
                        systemNotificationsEnabled =
                            settingsDataStore[SETTINGS_SYSTEM_NOTIFICATIONS_KEY] ?: false,
                    )
                }
            }
        }

    actual override suspend fun getSettings() = settings.firstOrNull()

    actual override suspend fun saveSettings(settings: Settings) {
        context.settingsDataStore.edit { settingsDataStore ->
            settingsDataStore[SETTINGS_USER_UUID_KEY] = settings.userUuid
            settingsDataStore[SETTINGS_PHONE_NUMBER_KEY] = settings.phoneNumber
            settingsDataStore[SETTINGS_EMAIL_KEY] = settings.email.orEmpty()
            settingsDataStore[SETTINGS_MARKETING_NOTIFICATIONS_KEY] = settings.marketingNotificationsEnabled
            settingsDataStore[SETTINGS_SYSTEM_NOTIFICATIONS_KEY] = settings.systemNotificationsEnabled
            val personalDiscountPercent = settings.personalDiscountPercent
            if (personalDiscountPercent != null) {
                settingsDataStore[SETTINGS_PERSONAL_DISCOUNT_PERCENT_KEY] = personalDiscountPercent
            } else {
                settingsDataStore.remove(SETTINGS_PERSONAL_DISCOUNT_PERCENT_KEY)
            }
        }
    }

    actual override val selectedPaymentMethodUuid: Flow<String?> =
        context.selectedPaymentMethodDataStore.data.map { selectedPaymentMethodDataStore ->
            selectedPaymentMethodDataStore[SELECTED_PAYMENT_METHOD_UUID_KEY]
        }

    actual override suspend fun saveSelectedPaymentMethodUuid(selectedPaymentMethodUuid: String) {
        context.selectedPaymentMethodDataStore.edit { selectedPaymentMethodDataStore ->
            selectedPaymentMethodDataStore[SELECTED_PAYMENT_METHOD_UUID_KEY] =
                selectedPaymentMethodUuid
        }
    }

    actual override val withoutUtensils: Flow<Boolean?> =
        context.withoutUtensilsDataStore.data.map { withoutUtensilsDataStore ->
            withoutUtensilsDataStore[WITHOUT_UTENSILS_KEY]
        }

    actual override suspend fun getWithoutUtensils(): Boolean? = withoutUtensils.firstOrNull()

    actual override suspend fun saveWithoutUtensils(withoutUtensils: Boolean) {
        context.withoutUtensilsDataStore.edit { withoutUtensilsDataStore ->
            withoutUtensilsDataStore[WITHOUT_UTENSILS_KEY] = withoutUtensils
        }
    }

    actual override suspend fun clearWithoutUtensils() {
        context.withoutUtensilsDataStore.edit { withoutUtensilsDataStore ->
            withoutUtensilsDataStore.clear()
        }
    }

    actual override val userUuid: Flow<String?> =
        context.userUuidDataStore.data.map { userUuidDataStore ->
            userUuidDataStore[USER_UUID_KEY]
        }

    actual override suspend fun getUserUuid() = userUuid.firstOrNull()

    actual override suspend fun saveUserUuid(userId: String) {
        context.userUuidDataStore.edit { userUuidDataStore ->
            userUuidDataStore[USER_UUID_KEY] = userId
        }
    }

    actual override suspend fun clearUserUuid() {
        context.userUuidDataStore.edit { userUuidDataStore ->
            userUuidDataStore.clear()
        }
    }

    actual override val selectedCityUuid: Flow<String?> =
        context.selectedCityDataStore.data.map { selectedCityDataStore ->
            selectedCityDataStore[SELECTED_CITY_UUID_KEY]
        }

    actual override suspend fun saveSelectedCityUuid(cityUuid: String) {
        context.selectedCityDataStore.edit { selectedCityDataStore ->
            selectedCityDataStore[SELECTED_CITY_UUID_KEY] = cityUuid
        }
    }

    actual override suspend fun getSelectedCityUuid(): String? =
        context.selectedCityDataStore.data
            .map { selectedCityDataStore ->
                selectedCityDataStore[SELECTED_CITY_UUID_KEY]
            }.firstOrNull()

    actual override fun observeUserAndCityUuid(): Flow<UserCityUuid> =
        userUuid.flatMapLatest { userUuid ->
            selectedCityUuid.map { cityUuid ->
                UserCityUuid(
                    userUuid = userUuid.orEmpty(),
                    cityUuid = cityUuid.orEmpty(),
                )
            }
        }

    actual override suspend fun getUserAndCityUuid(): UserCityUuid =
        UserCityUuid(
            userUuid = getUserUuid().orEmpty(),
            cityUuid = getSelectedCityUuid().orEmpty(),
        )

    actual override val discount: Flow<Discount?> =
        context.discountDataStore.data.map { discountDataStore ->
            discountDataStore[FIRST_ORDER_DISCOUNT_KEY]?.let { firstOrderDiscount ->
                Discount(
                    firstOrderDiscount = firstOrderDiscount,
                )
            }
        }

    actual override suspend fun getDiscount(): Discount? = discount.firstOrNull()

    actual override suspend fun saveDiscount(discount: Discount) {
        context.discountDataStore.edit { discountDataStore ->
            discountDataStore.remove(FIRST_ORDER_DISCOUNT_KEY)
            discount.firstOrderDiscount?.let { firstOrderDiscount ->
                discountDataStore[FIRST_ORDER_DISCOUNT_KEY] = firstOrderDiscount
            }
        }
    }

    actual override val recommendationMaxVisible: Flow<Int?> =
        context.recommendationDataStore.data.map { recommendationDataStore ->
            recommendationDataStore[RECOMMENDATION_MAX_VISIBLE_KEY]
        }

    actual override suspend fun getRecommendationMaxVisible() = recommendationMaxVisible.firstOrNull()

    actual override suspend fun saveRecommendationMaxVisible(recommendationMaxVisible: Int) {
        context.recommendationDataStore.edit { recommendationDataStore ->
            recommendationDataStore[RECOMMENDATION_MAX_VISIBLE_KEY] = recommendationMaxVisible
        }
    }

    actual override val userCafeUuid: Flow<String?> =
        context.userCafeUuid.data.map { userCafeDataStore ->
            userCafeDataStore[USER_CAFE_UUID_KEY]
        }

    actual override suspend fun getUserCafeUuid(): String? = userCafeUuid.firstOrNull()

    actual override suspend fun saveUserCafeUuid(userCafeUuid: String) {
        context.userCafeUuid.edit { recommendationDataStore ->
            recommendationDataStore[USER_CAFE_UUID_KEY] = userCafeUuid
        }
    }

    actual override suspend fun clearUserData() {
        context.tokenDataStore.edit { tokenDataStore ->
            tokenDataStore.clear()
        }
        context.userUuidDataStore.edit { userUuidDataStore ->
            userUuidDataStore.clear()
        }
        context.settingsDataStore.edit { settingsDataStore ->
            settingsDataStore.clear()
        }
        context.userCafeUuid.edit { userCafeUuid ->
            userCafeUuid.clear()
        }
        clearWithoutUtensils()
    }

    companion object {
        private const val TOKEN_DATA_STORE = "token data store"
        private const val TOKEN = "token"
        private val TOKEN_KEY = stringPreferencesKey(TOKEN)

        private const val SETTINGS_DATA_STORE = "settings data store"
        private const val SETTINGS_USER_UUID = "settings user uuid"
        private const val SETTINGS_PHONE_NUMBER = "settings phone number"
        private const val SETTINGS_EMAIL = "settings email"
        private const val SETTINGS_PERSONAL_DISCOUNT_PERCENT = "settings personal discount percent"
        private val SETTINGS_USER_UUID_KEY = stringPreferencesKey(SETTINGS_USER_UUID)
        private val SETTINGS_PHONE_NUMBER_KEY = stringPreferencesKey(SETTINGS_PHONE_NUMBER)
        private val SETTINGS_EMAIL_KEY = stringPreferencesKey(SETTINGS_EMAIL)
        private val SETTINGS_PERSONAL_DISCOUNT_PERCENT_KEY =
            intPreferencesKey(SETTINGS_PERSONAL_DISCOUNT_PERCENT)
        private val SETTINGS_MARKETING_NOTIFICATIONS_KEY =
            booleanPreferencesKey("settings marketing notifications")
        private val SETTINGS_SYSTEM_NOTIFICATIONS_KEY =
            booleanPreferencesKey("settings system notifications")

        private const val USER_UUID_DATA_STORE = "user id data store"
        private const val USER_UUID = "user uuid"
        private val USER_UUID_KEY = stringPreferencesKey(USER_UUID)

        private const val SELECTED_CITY_DATA_STORE = "selected city data store"
        private const val SELECTED_CITY_UUID = "selected city uuid"
        private val SELECTED_CITY_UUID_KEY = stringPreferencesKey(SELECTED_CITY_UUID)

        private const val SELECTED_PAYMENT_METHOD_DATA_STORE = "payment method data store"
        private const val SELECTED_PAYMENT_METHOD_UUID = "payment method uuid"
        private val SELECTED_PAYMENT_METHOD_UUID_KEY =
            stringPreferencesKey(SELECTED_PAYMENT_METHOD_UUID)

        private const val WITHOUT_UTENSILS_DATA_STORE = "without utensils data store"
        private const val WITHOUT_UTENSILS = "without utensils"
        private val WITHOUT_UTENSILS_KEY = booleanPreferencesKey(WITHOUT_UTENSILS)

        private const val DISCOUNT_DATA_STORE = "discount data store"
        private const val FIRST_ORDER_DISCOUNT = "first order discount"
        private val FIRST_ORDER_DISCOUNT_KEY = intPreferencesKey(FIRST_ORDER_DISCOUNT)

        private const val RECOMMENDATION_DATA_STORE = "recommendation"
        private const val RECOMMENDATION_MAX_VISIBLE = "recommendation max visible"
        private val RECOMMENDATION_MAX_VISIBLE_KEY = intPreferencesKey(RECOMMENDATION_MAX_VISIBLE)

        private const val USER_CAFE_DATA_STORE = "userCafe"
        private const val USER_CAFE_UUID = "user cafe uuid"
        private val USER_CAFE_UUID_KEY = stringPreferencesKey(USER_CAFE_UUID)
    }
}
