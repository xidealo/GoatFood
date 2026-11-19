package com.bunbeauty.core.domain.menu_product

import com.bunbeauty.core.domain.cafe.GetSelectedCafeUseCase
import com.bunbeauty.core.domain.repo.MenuProductRepo
import com.bunbeauty.core.model.addition.AdditionGroup
import com.bunbeauty.core.model.product.MenuProduct

class GetMenuProductUseCase(
    private val menuProductRepo: MenuProductRepo,
    private val getSelectedCafeUseCase: GetSelectedCafeUseCase,
) {
    suspend operator fun invoke(menuProductUuid: String): MenuProduct? {
        val cafeUuid = getSelectedCafeUseCase().uuid
        val menuProduct =
            menuProductRepo.getMenuProductByUuid(
                menuProductUuid = menuProductUuid,
                cafeUuid = cafeUuid,
            )

        return menuProduct?.copy(
            additionGroups = getAdditionGroupList(menuProduct),
        )
    }

    private fun getAdditionGroupList(menuProduct: MenuProduct) =
        menuProduct.additionGroups
            .map { additionGroup ->
                additionGroup.copy(
                    additionList = getAdditionList(additionGroup),
                )
            }.filter { additionGroup ->
                additionGroup.isVisible
            }.sortedBy { additionGroup ->
                additionGroup.priority
            }

    private fun getAdditionList(additionGroup: AdditionGroup) =
        additionGroup.additionList
            .filter { addition ->
                addition.isVisible
            }.sortedBy { addition ->
                addition.priority
            }
}
