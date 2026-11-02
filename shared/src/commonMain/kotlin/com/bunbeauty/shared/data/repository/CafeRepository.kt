package com.bunbeauty.shared.data.repository

import com.bunbeauty.core.domain.exeptions.NoSelectedCityUuidException
import com.bunbeauty.core.domain.exeptions.NoUserUuidException
import com.bunbeauty.core.domain.repo.CafeRepo
import com.bunbeauty.core.extension.dataOrNull
import com.bunbeauty.core.model.cafe.Cafe
import com.bunbeauty.shared.DataStoreRepo
import com.bunbeauty.shared.data.dao.cafe.ICafeDao
import com.bunbeauty.shared.data.mapper.cafe.toCafe
import com.bunbeauty.shared.data.mapper.cafe.toCafeEntity
import com.bunbeauty.shared.data.network.api.NetworkConnector
import com.bunbeauty.shared.data.storage.CafeStorage
import com.bunbeauty.shared.db.SelectedCafeUuidEntity
import com.bunbeauty.shared.ioDispatcher
import kotlinx.coroutines.withContext

class CafeRepository(
    private val networkConnector: NetworkConnector,
    private val cafeDao: ICafeDao,
    private val cafeStorage: CafeStorage,
    private val dataStoreRepo: DataStoreRepo,
) : CafeRepo {
    private var cafeListCache: List<Cafe>? = null

    override suspend fun getCafeList(): List<Cafe> {
        val selectedCityUuid =
            dataStoreRepo.getSelectedCityUuid() ?: throw NoSelectedCityUuidException()

        val list =
            if (cafeListCache == null) {
                val cafeList =
                    networkConnector
                        .getCafeListByCityUuid(cityUuid = selectedCityUuid)
                        .dataOrNull()
                        ?.results
                        .also { cafeList ->
                            if (cafeList != null) {
                                withContext(ioDispatcher) {
                                    cafeDao.insertCafeList(
                                        cafeList.map { cafeListItem ->
                                            cafeListItem.toCafeEntity()
                                        },
                                    )
                                }
                            }
                        }?.map { cafeListItem ->
                            cafeListItem.toCafe()
                        } ?: cafeDao
                        .getCafeListByCityUuid(cityUuid = selectedCityUuid)
                        .map { cafeEntity ->
                            cafeEntity.toCafe()
                        }

                cafeListCache = cafeList

                cafeList
            } else {
                cafeListCache
            }

        return list ?: emptyList()
    }

    override suspend fun saveSelectedCafeUuid(cafeUuid: String) {
        val userUuid = dataStoreRepo.getUserUuid() ?: return
        val selectedCityUuid = dataStoreRepo.getSelectedCityUuid() ?: return

        val selectedCafeUuidEntity =
            SelectedCafeUuidEntity(
                userUuid = userUuid,
                cityUuid = selectedCityUuid,
                cafeUuid = cafeUuid,
            )
        cafeDao.insertSelectedCafeUuid(selectedCafeUuidEntity)
    }

    override suspend fun getCafeByUuid(cafeUuid: String): Cafe? =
        getCafeList().find { cafe ->
            cafe.uuid == cafeUuid
        }

    override suspend fun getSelectedCafeByUserAndCityUuid(): Cafe? {
        val cityUuid = dataStoreRepo.getSelectedCityUuid() ?: throw NoSelectedCityUuidException()
        val userUuid = dataStoreRepo.getUserUuid() ?: throw NoUserUuidException()

        return cafeDao.getSelectedCafeByUserAndCityUuid(userUuid, cityUuid)?.toCafe()
    }

    override fun clearCache() {
        cafeStorage.clear()
        cafeListCache = null
    }
}
