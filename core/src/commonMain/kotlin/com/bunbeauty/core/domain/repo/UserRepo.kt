package com.bunbeauty.core.domain.repo

import com.bunbeauty.core.model.profile.Profile
import com.bunbeauty.core.model.profile.User
import kotlinx.coroutines.flow.Flow

interface UserRepo {
    val token: Flow<String?>

    fun observeUserByUuid(userUuid: String): Flow<User?>

    suspend fun getProfile(): Profile.Authorized?

    suspend fun clearUserCache()

    suspend fun getToken(): String?

    suspend fun disableUser()

    fun updateNotificationToken(notificationToken: String)
    suspend fun saveUserCafeUuid(cafeUuid: String)

    suspend fun updateNotificationTokenSuspend(notificationToken: String)

    fun updateNotificationPermission(enabled: Boolean)

    suspend fun updateNotificationPermissionSuspend(enabled: Boolean)

    suspend fun markNotificationOpened(notificationId: String)
}
