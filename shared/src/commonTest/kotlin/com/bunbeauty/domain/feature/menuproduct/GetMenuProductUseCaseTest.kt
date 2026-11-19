package com.bunbeauty.domain.feature.menuproduct

import com.bunbeauty.core.domain.cafe.GetSelectedCafeUseCase
import com.bunbeauty.core.domain.menu_product.GetMenuProductUseCase
import com.bunbeauty.core.domain.repo.MenuProductRepo
import com.bunbeauty.getAddition
import com.bunbeauty.getAdditionGroup
import com.bunbeauty.getCafe
import com.bunbeauty.getMenuProduct
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetMenuProductUseCaseTest {
    private val menuProductRepo: MenuProductRepo = mock()
    private val getSelectedCafeUseCase: GetSelectedCafeUseCase = mock()
    private val menuProductUuid = "menuProductUuid"
    private val cafeUuid = "cafeUuid"

    private val getMenuProductUseCase: GetMenuProductUseCase =
        GetMenuProductUseCase(
            menuProductRepo = menuProductRepo,
            getSelectedCafeUseCase = getSelectedCafeUseCase,
        )

    @Test
    fun `should return menu product with addition group list sorted by priority`() =
        runTest {
            // Given
            val additionGroup10 = getAdditionGroup(priority = 10)
            val additionGroup2 = getAdditionGroup(priority = 2)

            val initialAdditionGroups =
                listOf(
                    additionGroup10,
                    additionGroup2,
                )

            val initialMenuProduct =
                getMenuProduct(
                    additionGroups = initialAdditionGroups,
                )

            val menuProductWithSortedAdditionGroups =
                getMenuProduct(
                    additionGroups =
                        listOf(
                            additionGroup2,
                            additionGroup10,
                        ),
                )

            everySuspend { getSelectedCafeUseCase() } returns getCafe(cafeUuid)
            everySuspend {
                menuProductRepo.getMenuProductByUuid(
                    menuProductUuid = menuProductUuid,
                    cafeUuid = cafeUuid,
                )
            } returns initialMenuProduct

            // When
            val result = getMenuProductUseCase(menuProductUuid)

            // Then
            assertEquals(
                expected = menuProductWithSortedAdditionGroups,
                actual = result,
            )
        }

    @Test
    fun `should return menu product with addition group list filtered by isVisible`() =
        runTest {
            // Given
            val additionGroup10 = getAdditionGroup(priority = 10)
            val additionGroup2 = getAdditionGroup(priority = 2, isVisible = false)

            val initialAdditionGroups =
                listOf(
                    additionGroup10,
                    additionGroup2,
                )

            val initialMenuProduct =
                getMenuProduct(
                    additionGroups = initialAdditionGroups,
                )

            val menuProductWithFilteredAdditionGroups =
                getMenuProduct(
                    additionGroups =
                        listOf(
                            additionGroup10,
                        ),
                )

            everySuspend { getSelectedCafeUseCase() } returns getCafe(cafeUuid)
            everySuspend {
                menuProductRepo.getMenuProductByUuid(
                    menuProductUuid = menuProductUuid,
                    cafeUuid = cafeUuid,
                )
            } returns initialMenuProduct

            // When
            val result = getMenuProductUseCase(menuProductUuid)

            // Then
            assertEquals(
                expected = menuProductWithFilteredAdditionGroups,
                actual = result,
            )
        }

    @Test
    fun `should return menu product with addition list sorted by priority`() =
        runTest {
            // Given

            val addition10 = getAddition(priority = 10)
            val addition2 = getAddition(priority = 2)

            val initialAdditionGroups =
                listOf(
                    getAdditionGroup(
                        additions =
                            listOf(
                                addition10,
                                addition2,
                            ),
                    ),
                )

            val additionGroupsWithSortedAdditions =
                listOf(
                    getAdditionGroup(
                        additions =
                            listOf(
                                addition2,
                                addition10,
                            ),
                    ),
                )

            val initialMenuProduct =
                getMenuProduct(
                    additionGroups = initialAdditionGroups,
                )

            val menuProductWithSortedAdditionGroups =
                getMenuProduct(
                    additionGroups = additionGroupsWithSortedAdditions,
                )

            everySuspend { getSelectedCafeUseCase() } returns getCafe(cafeUuid)
            everySuspend {
                menuProductRepo.getMenuProductByUuid(
                    menuProductUuid = menuProductUuid,
                    cafeUuid = cafeUuid,
                )
            } returns initialMenuProduct

            // When
            val result = getMenuProductUseCase(menuProductUuid)

            // Then
            assertEquals(
                expected = menuProductWithSortedAdditionGroups,
                actual = result,
            )
        }

    @Test
    fun `should return menu product with addition list filttred by isVisible`() =
        runTest {
            // Given

            val addition10 = getAddition(priority = 10)
            val addition2 = getAddition(priority = 2, isVisible = false)

            val initialAdditionGroups =
                listOf(
                    getAdditionGroup(
                        additions =
                            listOf(
                                addition10,
                                addition2,
                            ),
                    ),
                )

            val additionGroupsWithFilttredAdditions =
                listOf(
                    getAdditionGroup(
                        additions =
                            listOf(
                                addition10,
                            ),
                    ),
                )

            val initialMenuProduct =
                getMenuProduct(
                    additionGroups = initialAdditionGroups,
                )

            val menuProductWithFilteredAdditionGroups =
                getMenuProduct(
                    additionGroups = additionGroupsWithFilttredAdditions,
                )

            everySuspend { getSelectedCafeUseCase() } returns getCafe(cafeUuid)
            everySuspend {
                menuProductRepo.getMenuProductByUuid(
                    menuProductUuid = menuProductUuid,
                    cafeUuid = cafeUuid,
                )
            } returns initialMenuProduct

            // When
            val result = getMenuProductUseCase(menuProductUuid)

            // Then
            assertEquals(
                expected = menuProductWithFilteredAdditionGroups,
                actual = result,
            )
        }
}
