package com.bunbeauty.domain.feature.cafe

import com.bunbeauty.core.domain.cafe.GetCafeListUseCase
import com.bunbeauty.core.domain.cafe.GetSelectedCafeUseCaseImpl
import com.bunbeauty.core.domain.cafe.IsPickupEnabledFromCafeUseCase
import com.bunbeauty.core.domain.exeptions.EmptyCafeListException
import com.bunbeauty.core.domain.repo.CafeRepo
import com.bunbeauty.getCafe
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetSelectedCafeUseCaseTest {
    private val cafeRepo: CafeRepo = mock()
    private val getCafeListUseCase: GetCafeListUseCase = mock()
    private val isPickupEnabledFromCafeUseCase: IsPickupEnabledFromCafeUseCase = mock()

    private val getSelectedCafeUseCase =
        GetSelectedCafeUseCaseImpl(
            cafeRepo = cafeRepo,
            getCafeListUseCase = getCafeListUseCase,
            isPickupEnabledFromCafeUseCase = isPickupEnabledFromCafeUseCase,
        )

    @Test
    fun `throws EmptyCafeListException when cafe list is empty`() =
        runTest {
            everySuspend { getCafeListUseCase() } returns emptyList()

            assertFailsWith<EmptyCafeListException> {
                getSelectedCafeUseCase()
            }
        }

    @Test
    fun `returns user selected cafe when present`() =
        runTest {
            val cafes =
                listOf(
                    getCafe("cafe1"),
                    getCafe("cafe2"),
                )
            val selectedCafe = cafes[1]

            everySuspend { getCafeListUseCase() } returns cafes
            everySuspend { cafeRepo.getSelectedCafeByUserAndCityUuid() } returns selectedCafe

            assertEquals(selectedCafe, getSelectedCafeUseCase())
        }

    @Test
    fun `returns first pickup-enabled cafe when no user selection`() =
        runTest {
            val cafes =
                listOf(
                    getCafe("cafe1"),
                    getCafe("cafe2"),
                    getCafe("cafe3"),
                )

            everySuspend { getCafeListUseCase() } returns cafes
            everySuspend { cafeRepo.getSelectedCafeByUserAndCityUuid() } returns null
            everySuspend { isPickupEnabledFromCafeUseCase("cafe1") } returns false
            everySuspend { isPickupEnabledFromCafeUseCase("cafe2") } returns true

            assertEquals(cafes[1], getSelectedCafeUseCase())
        }

    @Test
    fun `returns first cafe when no pickup-enabled cafes`() =
        runTest {
            val cafes =
                listOf(
                    getCafe("cafe1"),
                    getCafe("cafe2"),
                )

            everySuspend { getCafeListUseCase() } returns cafes
            everySuspend { cafeRepo.getSelectedCafeByUserAndCityUuid() } returns null
            everySuspend { isPickupEnabledFromCafeUseCase("cafe1") } returns false
            everySuspend { isPickupEnabledFromCafeUseCase("cafe2") } returns false

            assertEquals(cafes[0], getSelectedCafeUseCase())
        }
}
