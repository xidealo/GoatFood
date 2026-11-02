package com.bunbeauty.shared.data.network.model.order.get

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class OrderServer(
    @SerialName("uuid")
    val uuid: String,
    @SerialName("code")
    val code: String,
    @SerialName("status")
    val status: String,
    @SerialName("time")
    val time: Long,
    @SerialName("timeZone")
    val timeZone: String,
    @SerialName("isDelivery")
    val isDelivery: Boolean,
    @SerialName("deferredTime")
    val deferredTime: Long?,
    @SerialName("address")
    val address: OrderAddressServer,
    @SerialName("comment")
    val comment: String?,
    @SerialName("deliveryCost")
    val deliveryCost: Int?,
    @SerialName("clientUserUuid")
    val clientUserUuid: String,
    @SerialName("oderProductList")
    val oderProductList: List<OrderProductServer>,
    @SerialName("paymentMethod")
    val paymentMethod: String?,
    @SerialName("oldTotalCost")
    val oldTotalCost: Int?,
    @SerialName("newTotalCost")
    val newTotalCost: Int,
    @SerialName("percentDiscount")
    val percentDiscount: Int?,
    @SerialName("pickupServing")
    val pickupServing: String? = null,
)

@Serializable
class OrderAddressServer(
    @SerialName("description")
    val description: String?,
    @SerialName("street")
    val street: String?,
    @SerialName("house")
    val house: String?,
    @SerialName("flat")
    val flat: String?,
    @SerialName("entrance")
    val entrance: String?,
    @SerialName("floor")
    val floor: String?,
    @SerialName("comment")
    val comment: String?,
)

@Serializable
class OrderProductServer(
    @SerialName("uuid")
    val uuid: String,
    @SerialName("count")
    val count: Int,
    @SerialName("name")
    val name: String,
    @SerialName("newPrice")
    val newPrice: Int,
    @SerialName("oldPrice")
    val oldPrice: Int?,
    @SerialName("newCommonPrice")
    val newCommonPrice: Int,
    @SerialName("oldCommonPrice")
    val oldCommonPrice: Int?,
    @SerialName("newTotalCost")
    val newTotalCost: Int,
    @SerialName("oldTotalCost")
    val oldTotalCost: Int?,
    @SerialName("utils")
    val utils: String?,
    @SerialName("nutrition")
    val nutrition: Int?,
    @SerialName("description")
    val description: String,
    @SerialName("comboDescription")
    val comboDescription: String?,
    @SerialName("photoLink")
    val photoLink: String,
    @SerialName("barcode")
    val barcode: Int?,
    @SerialName("orderUuid")
    val orderUuid: String,
    @SerialName("additions")
    val additions: List<OrderAdditionServer>,
)

@Serializable
class OrderAdditionServer(
    @SerialName("uuid")
    val uuid: String,
    @SerialName("name")
    val name: String,
    @SerialName("priority")
    val priority: Int,
)
