package com.bunbeauty.shared.domain.feature.review

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private const val APP_REVIEW_DATA_STORE = "app review"
private val LAST_ORDER_UUID_KEY = stringPreferencesKey("last order uuid")
private val LAST_REQUEST_TIMESTAMP_KEY = longPreferencesKey("last request timestamp")

private val Context.appReviewDataStore: DataStore<Preferences> by preferencesDataStore(
    name = APP_REVIEW_DATA_STORE,
)

internal data class StoredAppReviewRequest(
    val orderUuid: String,
    val timestamp: Long,
)

internal interface AppReviewStorage {
    suspend fun getLastRequest(): StoredAppReviewRequest?

    suspend fun saveRequest(request: StoredAppReviewRequest)
}

internal class DataStoreAppReviewStorage(
    private val context: Context,
) : AppReviewStorage {
    override suspend fun getLastRequest(): StoredAppReviewRequest? {
        val preferences = context.appReviewDataStore.data.first()
        val orderUuid = preferences[LAST_ORDER_UUID_KEY] ?: return null
        val timestamp = preferences[LAST_REQUEST_TIMESTAMP_KEY] ?: return null
        return StoredAppReviewRequest(orderUuid = orderUuid, timestamp = timestamp)
    }

    override suspend fun saveRequest(request: StoredAppReviewRequest) {
        context.appReviewDataStore.edit { preferences ->
            preferences[LAST_ORDER_UUID_KEY] = request.orderUuid
            preferences[LAST_REQUEST_TIMESTAMP_KEY] = request.timestamp
        }
    }
}
