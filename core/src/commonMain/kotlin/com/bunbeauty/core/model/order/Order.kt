package com.bunbeauty.core.model.order

import com.bunbeauty.core.model.date_time.DateTime
import com.bunbeauty.core.model.date_time.Time
import com.bunbeauty.core.model.payment_method.PaymentMethodName
import com.bunbeauty.core.model.product.OrderProduct

data class Order(
    val uuid: String,
    val code: String,
    val status: OrderStatus,
    val dateTime: DateTime,
    val isDelivery: Boolean,
    val deferredTime: Time?,
    val address: OrderAddress,
    val comment: String?,
    val deliveryCost: Int?,
    val orderProductList: List<OrderProduct>,
    val paymentMethod: PaymentMethodName?,
    val oldTotalCost: Int?,
    val newTotalCost: Int,
    val percentDiscount: Int?,
    val pickupServing: PickupServing? = null,
)

data class OrderAddress(
    val description: String?,
    val street: String?,
    val house: String?,
    val flat: String?,
    val entrance: String?,
    val floor: String?,
    val comment: String?,
)
