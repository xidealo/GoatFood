package com.bunbeauty.core.model.order

import com.bunbeauty.core.model.product.CreatedOrderProduct

class CreatedOrder(
    val isDelivery: Boolean,
    val address: CreatedOrderAddress,
    val comment: String?,
    val deferredTime: Long?,
    val orderProducts: List<CreatedOrderProduct>,
    val paymentMethod: String?,
    val pickupServing: PickupServing?,
)

class CreatedOrderAddress(
    val uuid: String,
    val description: String? = null,
    val street: String? = null,
    val house: String? = null,
    val flat: String? = null,
    val entrance: String? = null,
    val floor: String? = null,
    val comment: String? = null,
)
