package com.bunbeauty.core.domain.order

import com.bunbeauty.core.domain.repo.CartProductRepo
import com.bunbeauty.core.domain.repo.OrderRepo
import com.bunbeauty.core.domain.repo.SettingsRepo
import com.bunbeauty.core.domain.util.DateTimeUtil
import com.bunbeauty.core.model.address.UserAddress
import com.bunbeauty.core.model.cafe.Cafe
import com.bunbeauty.core.model.cart.CartProduct
import com.bunbeauty.core.model.date_time.Time
import com.bunbeauty.core.model.order.CreatedOrder
import com.bunbeauty.core.model.order.CreatedOrderAddress
import com.bunbeauty.core.model.order.OrderCode
import com.bunbeauty.core.model.order.PickupServing
import com.bunbeauty.core.model.product.CreatedOrderProduct
import kotlin.time.ExperimentalTime

class CreateOrderUseCase(
    private val cartProductRepo: CartProductRepo,
    private val dateTimeUtil: DateTimeUtil,
    private val orderRepo: OrderRepo,
    private val settingsRepo: SettingsRepo,
) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(
        isDelivery: Boolean,
        selectedUserAddress: UserAddress?,
        selectedCafe: Cafe?,
        orderComment: String?,
        deferredTime: Time?,
        timeZone: String,
        paymentMethod: String?,
        pickupServing: PickupServing?,
    ): OrderCode? {
        if (!isPickupServingValid(isDelivery = isDelivery, pickupServing = pickupServing)) {
            return null
        }

        val cartProductList = cartProductRepo.getCartProductList()

        val createdOrderAddress =
            if (isDelivery) {
                selectedUserAddress ?: return null
                selectedUserAddress.run {
                    CreatedOrderAddress(
                        uuid = uuid,
                        street = street,
                        house = house,
                        flat = flat,
                        entrance = entrance,
                        floor = floor,
                        comment = comment,
                    )
                }
            } else {
                selectedCafe ?: return null
                CreatedOrderAddress(
                    uuid = selectedCafe.uuid,
                    description = selectedCafe.address,
                )
            }

        val createdOrder =
            CreatedOrder(
                isDelivery = isDelivery,
                address = createdOrderAddress,
                comment = orderComment,
                deferredTime =
                    deferredTime?.let {
                        dateTimeUtil.getMillisByTime(deferredTime, timeZone)
                    },
                orderProducts =
                    cartProductList.map { cartProduct ->
                        CreatedOrderProduct(
                            menuProductUuid = cartProduct.product.uuid,
                            count = cartProduct.count,
                            additionUuids = getSortedAdditionUuidList(cartProduct),
                        )
                    },
                paymentMethod = paymentMethod,
                pickupServing = pickupServing,
            )

        val orderCode = orderRepo.createOrder(createdOrder = createdOrder)
        if (orderCode != null) {
            settingsRepo.refreshSettings()
        }
        return orderCode
    }

    private fun isPickupServingValid(
        isDelivery: Boolean,
        pickupServing: PickupServing?,
    ): Boolean =
        if (isDelivery) {
            pickupServing == null
        } else {
            pickupServing != null
        }

    private fun getSortedAdditionUuidList(cartProduct: CartProduct) =
        cartProduct.additionList
            .sortedBy { cartProductAddition ->
                cartProductAddition.priority
            }.map { cartProductAddition ->
                cartProductAddition.additionUuid
            }
}
