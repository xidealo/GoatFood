package com.bunbeauty.shared.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CafeListItemServer(
    @SerialName("uuid")
    val uuid: String,
    @SerialName("fromTime")
    val fromTime: Int,
    @SerialName("toTime")
    val toTime: Int,
    @SerialName("phone")
    val phone: String,
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("longitude")
    val longitude: Double,
    @SerialName("address")
    val address: String,
    @SerialName("cityUuid")
    val cityUuid: String,
    @SerialName("isVisible")
    val isVisible: Boolean,
    @SerialName("workType")
    val workType: String,
    @SerialName("workload")
    val workload: String,
    @SerialName("additionalUtensils")
    val additionalUtensils: Boolean,
    @SerialName("pickupServingChoiceEnabled")
    val pickupServingChoiceEnabled: Boolean = false,
)
