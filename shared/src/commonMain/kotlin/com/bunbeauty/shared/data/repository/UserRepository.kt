package com.bunbeauty.shared.data.repository

import com.bunbeauty.core.Logger
import com.bunbeauty.core.domain.repo.UserRepo
import com.bunbeauty.core.model.profile.Profile
import com.bunbeauty.core.model.profile.User
import com.bunbeauty.shared.DataStoreRepo
import com.bunbeauty.shared.data.dao.order.IOrderDao
import com.bunbeauty.shared.data.dao.user.IUserDao
import com.bunbeauty.shared.data.dao.user_address.IUserAddressDao
import com.bunbeauty.shared.data.mapper.profile.IProfileMapper
import com.bunbeauty.shared.data.mapper.user.IUserMapper
import com.bunbeauty.shared.data.network.api.NetworkConnector
import com.bunbeauty.shared.data.network.model.NotificationOpenedRequest
import com.bunbeauty.shared.data.network.model.UpdateNotificationPermissionRequest
import com.bunbeauty.shared.data.network.model.UpdateNotificationTokenRequest
import com.bunbeauty.shared.data.network.model.profile.get.ProfileServer
import com.bunbeauty.shared.data.network.model.profile.patch.PatchUserServer
import com.bunbeauty.shared.domain.mapFlow
import com.bunbeauty.shared.ioDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class UserRepository(
    private val networkConnector: NetworkConnector,
    private val profileMapper: IProfileMapper,
    private val userMapper: IUserMapper,
    private val userDao: IUserDao,
    private val userAddressDao: IUserAddressDao,
    private val orderDao: IOrderDao,
    private val dataStoreRepo: DataStoreRepo,
) : DatabaseCacheRepository(),
    UserRepo,
    CoroutineScope {
    val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            Logger.logE("UserRepository", throwable.printStackTrace())
        }

    override val coroutineContext: CoroutineContext
        get() = SupervisorJob() + ioDispatcher + coroutineExceptionHandler

    override val tag: String = "USER_TAG"
    var cachedUserUuid: String? = null

    override val token: Flow<String?> = dataStoreRepo.token

    override fun observeUserByUuid(userUuid: String): Flow<User?> = userDao.observeUserByUuid(uuid = userUuid).mapFlow(userMapper::toUser)

    override suspend fun getProfile(): Profile.Authorized? {
        val token = dataStoreRepo.getToken() ?: return null

        return dataStoreRepo.getUserAndCityUuid().let { userCityUuid ->
            val profile =
                getCacheOrData(
                    isCacheValid = {
                        cachedUserUuid == userCityUuid.userUuid
                    },
                    onLocalRequest = {
                        getProfileLocally(
                            userUuid = userCityUuid.userUuid,
                            cityUuid = userCityUuid.cityUuid,
                        )
                    },
                    onApiRequest = {
                        networkConnector.getProfile(token = token)
                    },
                    onSaveLocally = ::saveProfileLocally,
                    serverToDomainModel = profileMapper::toProfile,
                )
            cachedUserUuid = profile?.userUuid
            profile
        }
    }

    override suspend fun clearUserCache() {
        dataStoreRepo.clearUserData()
        userDao.deleteAll()
    }

    override suspend fun getToken(): String? = dataStoreRepo.getToken()

    override suspend fun disableUser() {
        dataStoreRepo.getToken()?.let { token ->
            networkConnector.patchSettings(token, PatchUserServer(isActive = false))
        }
    }

    override fun updateNotificationToken(notificationToken: String) {
        launch {
            dataStoreRepo.getToken()?.let { token ->
                networkConnector.putNotificationToken(
                    updateNotificationTokenRequest =
                        UpdateNotificationTokenRequest(
                            token = notificationToken,
                        ),
                    token = token,
                )
            }
        }
    }

    override suspend fun saveUserCafeUuid(cafeUuid: String) {
        dataStoreRepo.saveUserCafeUuid(userCafeUuid = cafeUuid)
    }

    override suspend fun updateNotificationTokenSuspend(notificationToken: String) {
        dataStoreRepo.getToken()?.let { token ->
            networkConnector.putNotificationToken(
                updateNotificationTokenRequest =
                    UpdateNotificationTokenRequest(
                        token = notificationToken,
                    ),
                token = token,
            )
        }
    }

    override fun updateNotificationPermission(enabled: Boolean) {
        launch {
            updateNotificationPermissionSuspend(enabled)
        }
    }

    override suspend fun updateNotificationPermissionSuspend(enabled: Boolean) {
        dataStoreRepo.getToken()?.let { token ->
            networkConnector.putNotificationPermission(
                request = UpdateNotificationPermissionRequest(enabled),
                token = token,
            )
        }
    }

    override suspend fun markNotificationOpened(notificationId: String) {
        dataStoreRepo.getToken()?.let { token ->
            networkConnector.postNotificationOpened(
                request = NotificationOpenedRequest(notificationId),
                token = token,
            )
        }
    }

    private suspend fun saveProfileLocally(profile: ProfileServer?) {
        if (profile != null) {
            dataStoreRepo.saveUserUuid(profile.uuid)
            userDao.insertUser(profileMapper.toUserEntity(profile))
        }
    }

    private suspend fun getProfileLocally(
        userUuid: String,
        cityUuid: String,
    ): Profile.Authorized? =
        userDao.getUserByUuid(userUuid)?.let { userEntity ->
            val userAddressCount =
                userAddressDao.getUserAddressCountByUserAndCityUuid(userUuid, cityUuid)
            val lastOrderEntity =
                orderDao
                    .getOrderListByUserUuid(
                        userUuid = userUuid,
                        count = 1,
                    ).firstOrNull()
            profileMapper.toProfile(
                userUuid = userEntity.uuid,
                userAddressCount = userAddressCount,
                lastOrderEntity = lastOrderEntity,
            )
        }
}
