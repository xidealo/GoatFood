package com.bunbeauty.core.domain.repo

import com.bunbeauty.core.model.product.MenuProduct
import kotlinx.coroutines.flow.Flow

interface MenuProductRepo {
    suspend fun getMenuProductList(cafeUuid: String): List<MenuProduct>

    fun observeMenuProductList(): Flow<List<MenuProduct>>

    fun observeMenuProductByUuid(menuProductUuid: String): Flow<MenuProduct?>

    suspend fun getMenuProductByUuid(
        menuProductUuid: String,
        cafeUuid: String,
    ): MenuProduct?

    fun clearCache()
}
