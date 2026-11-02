package com.bunbeauty.shared.data.dao.cafe

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.bunbeauty.shared.db.CafeEntity
import com.bunbeauty.shared.db.FoodDeliveryDatabase
import com.bunbeauty.shared.db.SelectedCafeUuidEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class CafeDao(
    foodDeliveryDatabase: FoodDeliveryDatabase,
) : ICafeDao {
    private val cityEntityQueries = foodDeliveryDatabase.cafeEntityQueries
    private val selectedCafeUuidEntityQueries = foodDeliveryDatabase.selectedCafeUuidEntityQueries

    override suspend fun insertCafeList(cafeList: List<CafeEntity>) {
        cityEntityQueries.transaction {
            cafeList.onEach { cafeEntity ->
                cityEntityQueries.insertCafe(
                    uuid = cafeEntity.uuid,
                    fromTime = cafeEntity.fromTime,
                    toTime = cafeEntity.toTime,
                    offset = cafeEntity.offset,
                    phone = cafeEntity.phone,
                    latitude = cafeEntity.latitude,
                    longitude = cafeEntity.longitude,
                    address = cafeEntity.address,
                    cityUuid = cafeEntity.cityUuid,
                    isVisible = cafeEntity.isVisible,
                    workType = cafeEntity.workType,
                    workload = cafeEntity.workload,
                    additionalUtensils = cafeEntity.additionalUtensils,
                    pickupServingChoiceEnabled = cafeEntity.pickupServingChoiceEnabled,
                )
            }
        }
    }

    override suspend fun insertSelectedCafeUuid(selectedCafeUuidEntity: SelectedCafeUuidEntity) {
        selectedCafeUuidEntityQueries.insertSelectedCafeUuid(
            userUuid = selectedCafeUuidEntity.userUuid,
            cityUuid = selectedCafeUuidEntity.cityUuid,
            cafeUuid = selectedCafeUuidEntity.cafeUuid,
        )
    }

    override suspend fun getSelectedCafeByUserAndCityUuid(
        userUuid: String,
        cityUuid: String,
    ): CafeEntity? =
        cityEntityQueries
            .getSelectedCafeByUserAndCityUuid(userUuid, cityUuid)
            .awaitAsOneOrNull()

    override suspend fun getFirstCafeByCityUuid(cityUuid: String): CafeEntity? =
        cityEntityQueries.getFirstCafeByCityUuid(cityUuid).awaitAsOneOrNull()

    override fun observeCafeListByCityUuid(cityUuid: String): Flow<List<CafeEntity>> =
        cityEntityQueries
            .getCafeListByCityUuid(cityUuid)
            .asFlow()
            .mapToList(Dispatchers.Default)

    override fun observeCafeByUuid(uuid: String): Flow<CafeEntity?> =
        cityEntityQueries
            .getCafeByUuid(uuid)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)

    override fun observeSelectedCafeByUserAndCityUuid(
        userUuid: String,
        cityUuid: String,
    ): Flow<CafeEntity?> =
        cityEntityQueries
            .getSelectedCafeByUserAndCityUuid(userUuid, cityUuid)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)

    override fun observeFirstCafeByCityUuid(cityUuid: String): Flow<CafeEntity?> =
        cityEntityQueries
            .getFirstCafeByCityUuid(cityUuid)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)

    override suspend fun getCafeListByCityUuid(cityUuid: String): List<CafeEntity> =
        cityEntityQueries.getCafeListByCityUuid(cityUuid).awaitAsList()

    override suspend fun getCafeByUuid(uuid: String): CafeEntity? = cityEntityQueries.getCafeByUuid(uuid).awaitAsOneOrNull()
}
