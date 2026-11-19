package com.bunbeauty.shared.data.repository

import com.bunbeauty.core.domain.repo.MenuProductRepo
import com.bunbeauty.core.model.addition.AdditionGroup
import com.bunbeauty.core.model.product.MenuProduct
import com.bunbeauty.shared.data.dao.addition.IAdditionDao
import com.bunbeauty.shared.data.dao.addition_group.IAdditionGroupDao
import com.bunbeauty.shared.data.dao.category.ICategoryDao
import com.bunbeauty.shared.data.dao.menu_product.IMenuProductDao
import com.bunbeauty.shared.data.dao.menu_product_category_reference.IMenuProductCategoryReferenceDao
import com.bunbeauty.shared.data.mapper.addition.mapAdditionEntityToAddition
import com.bunbeauty.shared.data.mapper.additiongroup.mapAdditionGroupEntityToGroup
import com.bunbeauty.shared.data.mapper.menuProduct.IMenuProductMapper
import com.bunbeauty.shared.data.network.api.NetworkConnector
import com.bunbeauty.shared.data.network.model.MenuProductServer
import com.bunbeauty.shared.domain.mapFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MenuProductRepository(
    private val networkConnector: NetworkConnector,
    private val menuProductDao: IMenuProductDao,
    private val categoryDao: ICategoryDao,
    private val menuProductCategoryReferenceDao: IMenuProductCategoryReferenceDao,
    private val menuProductMapper: IMenuProductMapper,
    private val additionDao: IAdditionDao,
    private val additionGroupDao: IAdditionGroupDao,
) : CacheListRepository<MenuProduct>(),
    MenuProductRepo {
    override val tag: String = "MENU_PRODUCT_TAG"

    private var cachedCafeUuid: String? = null

    override suspend fun getMenuProductList(cafeUuid: String): List<MenuProduct> {
        val cacheData = cache
        if (cacheData != null && cachedCafeUuid == cafeUuid) {
            return cacheData
        }
        if (cachedCafeUuid != cafeUuid) {
            cache = null
            cachedCafeUuid = null
        }
        return getCacheOrListData(
            isCacheValid = { cachedCafeUuid == cafeUuid },
            onApiRequest = { networkConnector.getMenuProductListByCafe(cafeUuid) },
            onLocalRequest = ::loadMenuProductListLocally,
            onSaveLocally = ::saveMenuLocally,
            serverToDomainModel = menuProductMapper::toMenuProduct,
        ).also {
            if (cache != null) {
                cachedCafeUuid = cafeUuid
            }
        }
    }

    private suspend fun loadMenuProductListLocally(): List<MenuProduct> =
        menuProductMapper
            .toMenuProductList(
                menuProductDao.getMenuProductWithCategoryList(),
            ).map { menuProduct ->
                menuProduct.copy(
                    additionGroups = getAdditionGroups(menuProduct),
                )
            }

    private suspend fun getAdditionGroups(menuProduct: MenuProduct) =
        additionGroupDao
            .getAdditionGroupEntityList(
                menuProduct = menuProduct.uuid,
            ).map(mapAdditionGroupEntityToGroup)
            .map { additionGroup ->
                additionGroup.copy(
                    additionList = getAdditions(additionGroup),
                )
            }

    override fun observeMenuProductList(): Flow<List<MenuProduct>> =
        menuProductDao.observeMenuProductList().map { menuProductWithCategoryEntityList ->
            menuProductMapper.toMenuProductList(menuProductWithCategoryEntityList)
        }

    override fun observeMenuProductByUuid(menuProductUuid: String): Flow<MenuProduct?> =
        menuProductDao
            .observeMenuProductByUuid(menuProductUuid)
            .mapFlow(menuProductMapper::toMenuProduct)

    override suspend fun getMenuProductByUuid(
        menuProductUuid: String,
        cafeUuid: String,
    ): MenuProduct? =
        getMenuProductList(cafeUuid = cafeUuid).find { menuProduct ->
            menuProduct.uuid == menuProductUuid
        }

    override fun clearCache() {
        cache = null
        cachedCafeUuid = null
    }

    private suspend fun getAdditions(additionGroup: AdditionGroup) =
        additionDao
            .getAdditionEntityListByAdditionGroup(
                additionGroup.uuid,
            ).map(mapAdditionEntityToAddition)

    private suspend fun saveMenuLocally(menuProductServerList: List<MenuProductServer>) {
        menuProductMapper
            .toCategoryEntityList(menuProductServerList)
            .let { categoryEntityList ->
                categoryDao.insertCategoryList(categoryEntityList)
            }
        menuProductServerList
            .map(menuProductMapper::toMenuProductEntity)
            .let { menuProductEntityList ->
                menuProductDao.insertMenuProductList(menuProductEntityList)
            }

        menuProductMapper
            .toAdditionGroupEntityList(menuProductServerList)
            .let { additionGroupEntityList ->
                additionGroupDao.insertList(additionGroupEntityList)
            }

        menuProductMapper
            .toAdditionEntityList(menuProductServerList)
            .let { additionEntityList ->
                additionDao.insertList(additionEntityList)
            }

        menuProductServerList
            .flatMap(menuProductMapper::toMenuProductCategoryReference)
            .let { menuProductCategoryReferenceList ->
                menuProductCategoryReferenceDao.updateMenuProductReferenceList(
                    menuProductCategoryReferenceList,
                )
            }
    }
}
