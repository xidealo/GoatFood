package com.bunbeauty.domain.feature.menuproduct

import com.bunbeauty.core.domain.cafe.GetSelectedCafeUseCase
import com.bunbeauty.core.domain.menu_product.GetMenuProductListUseCaseImpl
import com.bunbeauty.core.domain.repo.MenuProductRepo
import com.bunbeauty.core.model.addition.AdditionGroup
import com.bunbeauty.core.model.category.Category
import com.bunbeauty.core.model.product.MenuProduct
import com.bunbeauty.getCafe
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetMenuProductListUseCaseTest {
    private val menuProductRepo: MenuProductRepo = mock()
    private val getSelectedCafeUseCase: GetSelectedCafeUseCase = mock()
    private val cafeUuid = "cafeUuid"

    private var getMenuProductListUseCase: GetMenuProductListUseCaseImpl =
        GetMenuProductListUseCaseImpl(
            menuProductRepo = menuProductRepo,
            getSelectedCafeUseCase = getSelectedCafeUseCase,
        )

    @Test
    fun `return only visible menu products for selected cafe`() =
        runTest {
            val menuProduct1 = getFakeMenuProduct(uuid = "1", visible = true)
            val menuProduct2 = getFakeMenuProduct(uuid = "2", visible = false)
            val menuProduct3 = getFakeMenuProduct(uuid = "3", visible = true)
            val initialList =
                listOf(
                    menuProduct1,
                    menuProduct2,
                    menuProduct3,
                )
            everySuspend { getSelectedCafeUseCase() } returns getCafe(cafeUuid)
            everySuspend { menuProductRepo.getMenuProductList(cafeUuid) } returns initialList
            val expectedList =
                listOf(
                    menuProduct1,
                    menuProduct3,
                )

            val resultMenuProductList = getMenuProductListUseCase()

            assertEquals(expectedList, resultMenuProductList)
        }

    @Test
    fun `return menu products with only visible addition groups`() =
        runTest {
            val additionGroup11 = getFakeAdditionGroup(uuid = "1.1", isVisible = true)
            val additionGroup12 = getFakeAdditionGroup(uuid = "1.2", isVisible = false)
            val additionGroup13 = getFakeAdditionGroup(uuid = "1.3", isVisible = true)
            val additionGroup21 = getFakeAdditionGroup(uuid = "2.1", isVisible = true)
            val additionGroup22 = getFakeAdditionGroup(uuid = "2.2", isVisible = false)
            val menuProduct1 =
                getFakeMenuProduct(
                    uuid = "1",
                    visible = true,
                    additionGroups =
                        listOf(
                            additionGroup11,
                            additionGroup12,
                            additionGroup13,
                        ),
                )
            val menuProduct2 =
                getFakeMenuProduct(
                    uuid = "2",
                    visible = true,
                    additionGroups =
                        listOf(
                            additionGroup21,
                            additionGroup22,
                        ),
                )
            val initialList =
                listOf(
                    menuProduct1,
                    menuProduct2,
                )
            everySuspend { getSelectedCafeUseCase() } returns getCafe(cafeUuid)
            everySuspend { menuProductRepo.getMenuProductList(cafeUuid) } returns initialList

            val updatedMenuProduct1 =
                getFakeMenuProduct(
                    uuid = "1",
                    visible = true,
                    additionGroups =
                        listOf(
                            additionGroup11,
                            additionGroup13,
                        ),
                )
            val updatedMenuProduct2 =
                getFakeMenuProduct(
                    uuid = "2",
                    visible = true,
                    additionGroups = listOf(additionGroup21),
                )
            val expectedList =
                listOf(
                    updatedMenuProduct1,
                    updatedMenuProduct2,
                )

            val resultMenuProductList = getMenuProductListUseCase()

            assertEquals(expectedList, resultMenuProductList)
        }

    private fun getFakeMenuProduct(
        uuid: String,
        visible: Boolean,
        additionGroups: List<AdditionGroup> = emptyList(),
    ): MenuProduct =
        MenuProduct(
            uuid = uuid,
            name = "name",
            newPrice = 0,
            oldPrice = 1,
            utils = "utils",
            nutrition = 1,
            description = "description",
            comboDescription = "comboDescription",
            photoLink = "photoLink",
            categoryList =
                listOf(
                    Category(
                        uuid = "uuid",
                        name = "free",
                        priority = 1,
                    ),
                ),
            visible = visible,
            isRecommended = false,
            additionGroups = additionGroups,
        )

    private fun getFakeAdditionGroup(
        uuid: String,
        isVisible: Boolean,
    ): AdditionGroup =
        AdditionGroup(
            uuid = uuid,
            additionList = emptyList(),
            isVisible = isVisible,
            name = "name",
            singleChoice = true,
            priority = 1,
        )
}
