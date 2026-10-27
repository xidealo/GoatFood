package com.bunbeauty.shared.data.network.api

import com.bunbeauty.core.ApiResult
import com.bunbeauty.shared.data.network.model.AddressServer
import com.bunbeauty.shared.data.network.model.CafeServer
import com.bunbeauty.shared.data.network.model.CategoryServer
import com.bunbeauty.shared.data.network.model.CityServer
import com.bunbeauty.shared.data.network.model.DiscountServer
import com.bunbeauty.shared.data.network.model.FavoriteServer
import com.bunbeauty.shared.data.network.model.PostFavoriteServer
import com.bunbeauty.shared.data.network.model.ForceUpdateVersionServer
import com.bunbeauty.shared.data.network.model.LinkServer
import com.bunbeauty.shared.data.network.model.ListServer
import com.bunbeauty.shared.data.network.model.MenuProductServer
import com.bunbeauty.shared.data.network.model.PaymentMethodServer
import com.bunbeauty.shared.data.network.model.PaymentServer
import com.bunbeauty.shared.data.network.model.RecommendationDataServer
import com.bunbeauty.shared.data.network.model.SettingsServer
import com.bunbeauty.shared.data.network.model.StreetServer
import com.bunbeauty.shared.data.network.model.SuggestionServer
import com.bunbeauty.shared.data.network.model.UpdateNotificationTokenRequest
import com.bunbeauty.shared.data.network.model.UserAddressPostServer
import com.bunbeauty.shared.data.network.model.login.AuthResponseServer
import com.bunbeauty.shared.data.network.model.login.AuthSessionServer
import com.bunbeauty.shared.data.network.model.login.CodeRequestServer
import com.bunbeauty.shared.data.network.model.login.CodeServer
import com.bunbeauty.shared.data.network.model.order.get.LightOrderServer
import com.bunbeauty.shared.data.network.model.order.get.OrderCodeServer
import com.bunbeauty.shared.data.network.model.order.get.OrderServer
import com.bunbeauty.shared.data.network.model.order.get.OrderUpdateServer
import com.bunbeauty.shared.data.network.model.order.post.OrderV6PostServer
import com.bunbeauty.shared.data.network.model.profile.get.ProfileServer
import com.bunbeauty.shared.data.network.model.profile.patch.PatchUserServer
import kotlinx.coroutines.flow.Flow

interface NetworkConnector {
    suspend fun getForceUpdateVersion(): ApiResult<ForceUpdateVersionServer>

    suspend fun getCategoryList(): ApiResult<ListServer<CategoryServer>>

    suspend fun getMenuProductList(): ApiResult<ListServer<MenuProductServer>>

    suspend fun getCityList(): ApiResult<ListServer<CityServer>>

    suspend fun getCafeListByCityUuid(cityUuid: String): ApiResult<ListServer<CafeServer>>

    suspend fun getStreetListByCityUuid(cityUuid: String): ApiResult<ListServer<StreetServer>>

    suspend fun getDiscount(): ApiResult<DiscountServer>

    suspend fun getSuggestions(
        token: String,
        query: String,
        cityUuid: String,
    ): ApiResult<ListServer<SuggestionServer>>

    suspend fun getUserAddressListByCityUuid(
        token: String,
        cityUuid: String,
    ): ApiResult<ListServer<AddressServer>>

    suspend fun getPayment(token: String): ApiResult<PaymentServer>

    suspend fun getProfile(token: String): ApiResult<ProfileServer>

    suspend fun getLightOrderList(
        token: String,
        count: Int? = null,
    ): ApiResult<ListServer<LightOrderServer>>

    suspend fun getOrderByUuid(
        token: String,
        uuid: String,
    ): ApiResult<OrderServer>

    suspend fun getLastOrder(token: String): ApiResult<LightOrderServer>

    suspend fun getSettings(token: String): ApiResult<SettingsServer>

    suspend fun getPaymentMethodList(): ApiResult<ListServer<PaymentMethodServer>>

    suspend fun getLinkList(): ApiResult<ListServer<LinkServer>>

    suspend fun getRecommendationData(): ApiResult<RecommendationDataServer>

    suspend fun getFavoriteList(token: String): ApiResult<ListServer<FavoriteServer>>

    suspend fun postFavorite(
        token: String,
        body: PostFavoriteServer,
    ): ApiResult<FavoriteServer>

    suspend fun deleteFavorite(
        token: String,
        menuProductUuid: String,
    ): ApiResult<Unit>

    suspend fun postUserAddress(
        token: String,
        userAddress: UserAddressPostServer,
    ): ApiResult<AddressServer>

    suspend fun putNotificationToken(
        updateNotificationTokenRequest: UpdateNotificationTokenRequest,
        token: String,
    ): ApiResult<Unit>

    suspend fun postOrder(
        token: String,
        order: OrderV6PostServer,
    ): ApiResult<OrderCodeServer>

    suspend fun postCodeRequest(codeRequest: CodeRequestServer): ApiResult<AuthSessionServer>

    suspend fun patchSettings(
        token: String,
        patchUserServer: PatchUserServer,
    ): ApiResult<SettingsServer>

    suspend fun putCodeResend(uuid: String): ApiResult<Unit>

    suspend fun putCodeCheck(
        code: CodeServer,
        uuid: String,
    ): ApiResult<AuthResponseServer>

    suspend fun startOrderUpdatesObservation(token: String): Pair<String?, Flow<OrderUpdateServer>>

    suspend fun stopOrderUpdatesObservation(uuid: String)
}
