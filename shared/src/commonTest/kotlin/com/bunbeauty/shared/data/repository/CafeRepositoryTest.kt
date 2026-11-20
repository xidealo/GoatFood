package com.bunbeauty.shared.data.repository

import com.bunbeauty.core.domain.exeptions.NoSelectedCityUuidException
import com.bunbeauty.shared.DataStoreRepo
import com.bunbeauty.shared.data.dao.cafe.ICafeDao
import com.bunbeauty.shared.data.network.api.NetworkConnector
import com.bunbeauty.shared.data.storage.CafeStorage
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class CafeRepositoryTest {
    private val networkConnector: NetworkConnector = mock()
    private val cafeDao: ICafeDao = mock()
    private val dataStoreRepo: DataStoreRepo = mock()

    private val cafeRepository =
        CafeRepository(
            networkConnector = networkConnector,
            cafeDao = cafeDao,
            cafeStorage = CafeStorage(),
            dataStoreRepo = dataStoreRepo,
        )

    @Test
    fun `returns null when guest has no saved cafe`() =
        runTest {
            everySuspend { dataStoreRepo.getSelectedCityUuid() } returns "city"
            everySuspend { dataStoreRepo.getUserUuid() } returns null

            val selectedCafe = cafeRepository.getSelectedCafeByUserAndCityUuid()

            assertNull(selectedCafe)
        }

    @Test
    fun `throws when city is not selected`() =
        runTest {
            everySuspend { dataStoreRepo.getSelectedCityUuid() } returns null

            assertFailsWith<NoSelectedCityUuidException> {
                cafeRepository.getSelectedCafeByUserAndCityUuid()
            }
        }
}
