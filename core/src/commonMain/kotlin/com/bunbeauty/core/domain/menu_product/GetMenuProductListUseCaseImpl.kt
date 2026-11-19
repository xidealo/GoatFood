package com.bunbeauty.core.domain.menu_product

import com.bunbeauty.core.domain.cafe.GetSelectedCafeUseCase
import com.bunbeauty.core.domain.repo.MenuProductRepo
import com.bunbeauty.core.model.product.MenuProduct

interface GetMenuProductListUseCase {
    suspend operator fun invoke(): List<MenuProduct>
}

class GetMenuProductListUseCaseImpl(
    private val menuProductRepo: MenuProductRepo,
    private val getSelectedCafeUseCase: GetSelectedCafeUseCase,
) : GetMenuProductListUseCase {
    override suspend operator fun invoke(): List<MenuProduct> {
        val cafeUuid = getSelectedCafeUseCase().uuid
        return menuProductRepo
            .getMenuProductList(cafeUuid = cafeUuid)
            .filter { menuProduct ->
                menuProduct.visible
            }.map { menuProduct ->
                menuProduct.copy(
                    additionGroups =
                        menuProduct.additionGroups.filter { additionGroup ->
                            additionGroup.isVisible
                        },
                )
            }
    }
}
