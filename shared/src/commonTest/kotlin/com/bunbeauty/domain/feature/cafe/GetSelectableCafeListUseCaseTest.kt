package com.bunbeauty.domain.feature.cafe

import com.bunbeauty.core.domain.cafe.GetCafeListUseCase
import com.bunbeauty.core.domain.cafe.GetSelectableCafeListUseCase
import com.bunbeauty.core.domain.cafe.GetSelectedCafeUseCase
import com.bunbeauty.core.domain.cafe.IsPickupEnabledFromCafeUseCase
import com.bunbeauty.core.domain.exeptions.EmptyCafeListException
import com.bunbeauty.core.model.cafe.SelectableCafe
import com.bunbeauty.getCafe
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetSelectableCafeListUseCaseTest {
    private val getCafeListUseCase: GetCafeListUseCase = mock()
    private val getSelectedCafeUseCase: GetSelectedCafeUseCase = mock()
    private var isPickupEnabledFromCafeUseCase: IsPickupEnabledFromCafeUseCase = mock()

    private val getSelectableCafeListUseCase: GetSelectableCafeListUseCase =
        GetSelectableCafeListUseCase(
            getCafeListUseCase = getCafeListUseCase,
            getSelectedCafeUseCase = getSelectedCafeUseCase,
            isPickupEnabledFromCafeUseCaseImpl = isPickupEnabledFromCafeUseCase,
        )

    @Test
    fun `invoke should throw EmptyCafeListException when cafe list is empty`() =
        runTest {
            // Arrange
            everySuspend { getSelectedCafeUseCase() } throws EmptyCafeListException()

            // Act & Assert
            assertFailsWith<EmptyCafeListException> {
                getSelectableCafeListUseCase()
            }
        }

    @Test
    fun `invoke should return sorted list with selected cafe when user has selected cafe`() =
        runTest {
            // Arrange
            val cafes =
                listOf(
                    getCafe("cafe1"),
                    getCafe("cafe2"),
                    getCafe("cafe3"),
                )
            val selectedCafe = cafes[1]

            everySuspend { getCafeListUseCase() } returns cafes
            everySuspend { getSelectedCafeUseCase() } returns selectedCafe
            everySuspend { isPickupEnabledFromCafeUseCase("cafe1") } returns true
            everySuspend { isPickupEnabledFromCafeUseCase("cafe2") } returns false
            everySuspend { isPickupEnabledFromCafeUseCase("cafe3") } returns true

            val expected =
                listOf(
                    generateSelectableCafe(uuid = cafes[0].uuid, isSelected = false, canBePickup = true),
                    generateSelectableCafe(uuid = cafes[2].uuid, isSelected = false, canBePickup = true),
                    generateSelectableCafe(uuid = cafes[1].uuid, isSelected = true, canBePickup = false),
                )

            // Act
            val result = getSelectableCafeListUseCase()

            // Assert
            assertEquals(expected, result)
        }

    @Test
    fun `invoke should return list with first pickup-enabled cafe selected when no user selection`() =
        runTest {
            // Arrange
            val cafes =
                listOf(
                    getCafe("cafe1"),
                    getCafe("cafe2"),
                    getCafe("cafe3"),
                )
            val selectedCafe = cafes[1]

            everySuspend { getCafeListUseCase() } returns cafes
            everySuspend { getSelectedCafeUseCase() } returns selectedCafe
            everySuspend { isPickupEnabledFromCafeUseCase("cafe1") } returns false
            everySuspend { isPickupEnabledFromCafeUseCase("cafe2") } returns true
            everySuspend { isPickupEnabledFromCafeUseCase("cafe3") } returns true

            val expected =
                listOf(
                    generateSelectableCafe(cafes[1].uuid, isSelected = true, canBePickup = true),
                    generateSelectableCafe(cafes[2].uuid, isSelected = false, canBePickup = true),
                    generateSelectableCafe(cafes[0].uuid, isSelected = false, canBePickup = false),
                )

            // Act
            val result = getSelectableCafeListUseCase()

            // Assert
            assertEquals(expected, result)
        }

    @Test
    fun `invoke should return list with first cafe selected when no pickup-enabled cafes`() =
        runTest {
            // Arrange
            val cafes =
                listOf(
                    getCafe("cafe1"),
                    getCafe("cafe2"),
                )
            val selectedCafe = cafes[0]

            everySuspend { getCafeListUseCase() } returns cafes
            everySuspend { getSelectedCafeUseCase() } returns selectedCafe
            everySuspend { isPickupEnabledFromCafeUseCase("cafe1") } returns false
            everySuspend { isPickupEnabledFromCafeUseCase("cafe2") } returns false

            val expected =
                listOf(
                    generateSelectableCafe(cafes[0].uuid, isSelected = true, canBePickup = false),
                    generateSelectableCafe(cafes[1].uuid, isSelected = false, canBePickup = false),
                )

            // Act
            val result = getSelectableCafeListUseCase()

            // Assert
            assertEquals(expected, result)
        }

    private fun generateSelectableCafe(
        uuid: String,
        isSelected: Boolean,
        canBePickup: Boolean,
    ) = SelectableCafe(
        cafe = getCafe(uuid),
        isSelected = isSelected,
        canBePickup = canBePickup,
    )
}
