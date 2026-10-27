package com.bunbeauty.shared.data.network.api

import com.bunbeauty.core.ApiError
import com.bunbeauty.core.ApiResult
import com.bunbeauty.core.Constants.AUTHORIZATION_HEADER
import com.bunbeauty.core.Constants.CITY_UUID_PARAMETER
import com.bunbeauty.core.Constants.COMPANY_UUID_PARAMETER
import com.bunbeauty.core.Constants.QUERY_PARAMETER
import com.bunbeauty.core.Constants.UUID_PARAMETER
import com.bunbeauty.core.domain.exeptions.FoodDeliveryNetworkException
import com.bunbeauty.shared.data.CompanyUuidProvider
import com.bunbeauty.shared.data.network.logger.NetworkErrorLogPolicy
import com.bunbeauty.shared.data.network.logger.NetworkErrorLogger
import com.bunbeauty.shared.data.network.logger.isCoroutineCancellation
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
import com.bunbeauty.shared.data.network.socket.SocketService
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.timeout
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.path
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent

private const val COMMON_TIMEOUT = 7000L
private const val FORCE_UPDATE_TIMEOUT = 5000L

internal class NetworkConnectorImpl(
    private val client: HttpClient,
    private val socketService: SocketService,
    private val companyUuidProvider: CompanyUuidProvider,
    private val errorLogger: NetworkErrorLogger,
) : KoinComponent,
    NetworkConnector {
    // GET

    override suspend fun getForceUpdateVersion(): ApiResult<ForceUpdateVersionServer> =
        getData(
            path = "force_update_version",
            parameters = mapOf(COMPANY_UUID_PARAMETER to companyUuidProvider.companyUuid),
            timeout = FORCE_UPDATE_TIMEOUT,
        )

    override suspend fun getCategoryList(): ApiResult<ListServer<CategoryServer>> =
        getData(
            path = "category",
            parameters = mapOf(COMPANY_UUID_PARAMETER to companyUuidProvider.companyUuid),
        )

    override suspend fun getMenuProductList(): ApiResult<ListServer<MenuProductServer>> =
        getData(
            path = "menu_product",
            parameters = mapOf(COMPANY_UUID_PARAMETER to companyUuidProvider.companyUuid),
        )

    override suspend fun getCityList(): ApiResult<ListServer<CityServer>> =
        getData(
            path = "city",
            parameters = mapOf(COMPANY_UUID_PARAMETER to companyUuidProvider.companyUuid),
        )

    override suspend fun getCafeListByCityUuid(cityUuid: String): ApiResult<ListServer<CafeServer>> =
        getData(
            path = "cafe",
            parameters = hashMapOf(CITY_UUID_PARAMETER to cityUuid),
        )

    override suspend fun getStreetListByCityUuid(cityUuid: String): ApiResult<ListServer<StreetServer>> =
        getData(
            path = "street",
            parameters = hashMapOf(CITY_UUID_PARAMETER to cityUuid),
        )

    override suspend fun getDiscount(): ApiResult<DiscountServer> =
        getData(
            path = "discount",
            parameters = mapOf(COMPANY_UUID_PARAMETER to companyUuidProvider.companyUuid),
        )

    override suspend fun getSuggestions(
        token: String,
        query: String,
        cityUuid: String,
    ): ApiResult<ListServer<SuggestionServer>> =
        getData(
            path = "v2/street/suggestions",
            parameters =
                mapOf(
                    QUERY_PARAMETER to query,
                    CITY_UUID_PARAMETER to cityUuid,
                ),
            token = token,
        )

    override suspend fun getUserAddressListByCityUuid(
        token: String,
        cityUuid: String,
    ): ApiResult<ListServer<AddressServer>> =
        getData(
            path = "v2/address",
            parameters = mapOf(CITY_UUID_PARAMETER to cityUuid),
            token = token,
        )

    override suspend fun getPayment(token: String): ApiResult<PaymentServer> =
        getData(
            path = "payment",
            token = token,
        )

    override suspend fun getProfile(token: String): ApiResult<ProfileServer> =
        getData(
            path = "client",
            token = token,
        )

    override suspend fun getLightOrderList(
        token: String,
        count: Int?,
    ): ApiResult<ListServer<LightOrderServer>> =
        getData(
            path = "/client/order/light/list",
            parameters =
                buildMap {
                    if (count != null) {
                        put("count", count)
                    }
                },
            token = token,
        )

    override suspend fun getOrderByUuid(
        token: String,
        uuid: String,
    ): ApiResult<OrderServer> =
        getData(
            path = "/client/order",
            parameters =
                buildMap {
                    put("uuid", uuid)
                },
            token = token,
        )

    override suspend fun getLastOrder(token: String): ApiResult<LightOrderServer> =
        safeCall(notFoundAsNull = true) {
            client.get {
                buildRequest(
                    path = "v2/client/last_order",
                    token = token,
                    timeout = COMMON_TIMEOUT,
                )
            }
        }

    override suspend fun getSettings(token: String): ApiResult<SettingsServer> =
        getData(
            path = "client/settings",
            token = token,
        )

    override suspend fun getPaymentMethodList(): ApiResult<ListServer<PaymentMethodServer>> =
        getData(
            path = "payment_method",
            parameters = mapOf(COMPANY_UUID_PARAMETER to companyUuidProvider.companyUuid),
        )

    override suspend fun getLinkList(): ApiResult<ListServer<LinkServer>> =
        getData(
            path = "link",
            parameters = mapOf(COMPANY_UUID_PARAMETER to companyUuidProvider.companyUuid),
        )

    override suspend fun getRecommendationData(): ApiResult<RecommendationDataServer> =
        getData(
            path = "recommendation",
            parameters = mapOf(COMPANY_UUID_PARAMETER to companyUuidProvider.companyUuid),
        )

    override suspend fun getFavoriteList(token: String): ApiResult<ListServer<FavoriteServer>> =
        getData(
            path = "v2/client/favorite",
            parameters = mapOf(COMPANY_UUID_PARAMETER to companyUuidProvider.companyUuid),
            token = token,
        )

    // POST
    override suspend fun postUserAddress(
        token: String,
        userAddress: UserAddressPostServer,
    ): ApiResult<AddressServer> =
        postData(
            path = "v2/address",
            body = userAddress,
            token = token,
        )

    override suspend fun putNotificationToken(
        updateNotificationTokenRequest: UpdateNotificationTokenRequest,
        token: String,
    ): ApiResult<Unit> =
        putData(
            path = "client/notification_token",
            body = updateNotificationTokenRequest,
            token = token,
        )

    override suspend fun postOrder(
        token: String,
        order: OrderV6PostServer,
    ): ApiResult<OrderCodeServer> =
        postData(
            path = "v6/order",
            body = order,
            token = token,
        )

    override suspend fun postFavorite(
        token: String,
        body: PostFavoriteServer,
    ): ApiResult<FavoriteServer> =
        postData(
            path = "v2/client/favorite",
            parameters = mapOf(COMPANY_UUID_PARAMETER to companyUuidProvider.companyUuid),
            body = body,
            token = token,
        )

    override suspend fun postCodeRequest(codeRequest: CodeRequestServer): ApiResult<AuthSessionServer> =
        postData(
            path = "client/code_request",
            body = codeRequest,
            parameters = mapOf(COMPANY_UUID_PARAMETER to companyUuidProvider.companyUuid),
        )

    // PATCH

    override suspend fun patchSettings(
        token: String,
        patchUserServer: PatchUserServer,
    ): ApiResult<SettingsServer> =
        patchData(
            path = "client/settings",
            body = patchUserServer,
            token = token,
        )

    // PUT

    override suspend fun putCodeResend(uuid: String): ApiResult<Unit> =
        putData(
            path = "client/code_resend",
            parameters = mapOf(UUID_PARAMETER to uuid),
        )

    override suspend fun putCodeCheck(
        code: CodeServer,
        uuid: String,
    ): ApiResult<AuthResponseServer> =
        putData(
            path = "client/code_check",
            body = code,
            parameters = mapOf(UUID_PARAMETER to uuid),
        )

    // DELETE

    override suspend fun deleteFavorite(
        token: String,
        menuProductUuid: String,
    ): ApiResult<Unit> =
        deleteData(
            path = "v2/client/favorite/$menuProductUuid",
            parameters = mapOf(COMPANY_UUID_PARAMETER to companyUuidProvider.companyUuid),
            token = token,
        )

    // WEB_SOCKET

    override suspend fun startOrderUpdatesObservation(token: String): Pair<String?, Flow<OrderUpdateServer>> =
        socketService.observeSocketMessages(
            path = "client/order/v2/subscribe",
            serializer = OrderUpdateServer.serializer(),
            token = token,
        )

    override suspend fun stopOrderUpdatesObservation(uuid: String) {
        socketService.closeSession(uuid)
    }

    // COMMON

    private suspend inline fun <reified R> getData(
        path: String,
        parameters: Map<String, Any> = mapOf(),
        token: String? = null,
        timeout: Long = COMMON_TIMEOUT,
    ): ApiResult<R> =
        safeCall {
            client.get {
                buildRequest(
                    path = path,
                    parameters = parameters,
                    token = token,
                    timeout = timeout,
                )
            }
        }

    private suspend inline fun <reified R> postData(
        path: String,
        parameters: Map<String, String> = mapOf(),
        body: Any,
        token: String? = null,
        timeout: Long = COMMON_TIMEOUT,
    ): ApiResult<R> =
        safeCall {
            client.post {
                buildRequest(
                    path = path,
                    parameters = parameters,
                    body = body,
                    token = token,
                    timeout = timeout,
                )
            }
        }

    private suspend inline fun <reified R> patchData(
        path: String,
        body: Any,
        parameters: Map<String, String> = mapOf(),
        token: String? = null,
        timeout: Long = COMMON_TIMEOUT,
    ): ApiResult<R> =
        safeCall {
            client.patch {
                buildRequest(
                    path = path,
                    parameters = parameters,
                    body = body,
                    token = token,
                    timeout = timeout,
                )
            }
        }

    private suspend inline fun <reified R> putData(
        path: String,
        body: Any? = null,
        parameters: Map<String, String> = mapOf(),
        token: String? = null,
        timeout: Long = COMMON_TIMEOUT,
    ): ApiResult<R> =
        safeCall {
            client.put {
                buildRequest(
                    path = path,
                    parameters = parameters,
                    body = body,
                    token = token,
                    timeout = timeout,
                )
            }
        }

    private suspend fun deleteData(
        path: String,
        parameters: Map<String, Any> = mapOf(),
        token: String? = null,
        timeout: Long = COMMON_TIMEOUT,
    ): ApiResult<Unit> =
        safeCall {
            client.delete {
                buildRequest(
                    path = path,
                    parameters = parameters,
                    token = token,
                    timeout = timeout,
                )
            }
        }

    private suspend inline fun <reified R> safeCall(
        notFoundAsNull: Boolean = false,
        networkCall: () -> HttpResponse,
    ): ApiResult<R> =
        try {
            val response = networkCall()
            mapSuccessResponse(response, notFoundAsNull)
        } catch (exception: FoodDeliveryNetworkException) {
            throw exception
        } catch (exception: ClientRequestException) {
            mapClientRequestException(exception, notFoundAsNull)
        } catch (exception: Throwable) {
            if (exception.isCoroutineCancellation()) {
                throw exception
            }
            val message = exception.message.orEmpty()
            logNetworkWarningIfNeeded(code = 0, message = message, throwable = exception)
            ApiResult.Error(ApiError(0, message))
        }

    private suspend inline fun <reified R> mapSuccessResponse(
        response: HttpResponse,
        notFoundAsNull: Boolean,
    ): ApiResult<R> {
        if (response.status == HttpStatusCode.NotFound && notFoundAsNull) {
            return ApiResult.Success(null)
        }
        if (response.status.value >= HttpStatusCode.BadRequest.value) {
            val code = response.status.value
            val message = response.status.description
            logNetworkWarningIfNeeded(
                code = code,
                message = message,
                throwable = UnexpectedHttpStatusException(code, message),
            )
            return ApiResult.Error(ApiError(code, message))
        }
        return ApiResult.Success(decodeBody(response))
    }

    private inline fun <reified R> mapClientRequestException(
        exception: ClientRequestException,
        notFoundAsNull: Boolean,
    ): ApiResult<R> {
        val code = exception.response.status.value
        val message = exception.message ?: exception.response.status.description
        if (code == HttpStatusCode.NotFound.value && notFoundAsNull) {
            return ApiResult.Success(null)
        }
        logNetworkWarningIfNeeded(code = code, message = message, throwable = exception)
        return ApiResult.Error(ApiError(code, message))
    }

    private suspend inline fun <reified R> decodeBody(response: HttpResponse): R {
        if (R::class == Unit::class) {
            @Suppress("UNCHECKED_CAST")
            return Unit as R
        }
        return response.body()
    }

    private fun logNetworkWarningIfNeeded(
        code: Int,
        message: String,
        throwable: Throwable,
    ) {
        if (NetworkErrorLogPolicy.shouldLog(code, throwable)) {
            errorLogger.logWarning(code = code, message = message, throwable = throwable)
        }
    }

    private class UnexpectedHttpStatusException(
        code: Int,
        message: String,
    ) : Exception("Unexpected HTTP status $code: $message")

    private fun HttpRequestBuilder.buildRequest(
        path: String,
        parameters: Map<String, Any> = mapOf(),
        body: Any? = null,
        token: String? = null,
        timeout: Long,
    ) {
        if (body != null) {
            setBody(body)
        }
        url {
            path(path)
        }
        parameters.forEach { parameterMap ->
            parameter(parameterMap.key, parameterMap.value)
        }
        if (token != null) {
            header(AUTHORIZATION_HEADER, "Bearer $token")
        }
        timeout {
            requestTimeoutMillis = timeout
            connectTimeoutMillis = timeout
            socketTimeoutMillis = timeout
        }
    }
}
