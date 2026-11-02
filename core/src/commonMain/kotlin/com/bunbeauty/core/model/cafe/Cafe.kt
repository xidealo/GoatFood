package com.bunbeauty.core.model.cafe

data class Cafe(
    val uuid: String,
    val fromTime: Int,
    val toTime: Int,
    val phone: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val cityUuid: String,
    val isVisible: Boolean,
    val workType: WorkType,
    val workload: Workload,
    val additionalUtensils: Boolean,
    val pickupServingChoiceEnabled: Boolean = false,
) {
    enum class WorkType {
        DELIVERY,
        PICKUP,
        DELIVERY_AND_PICKUP,
        CLOSED,
    }

    enum class Workload {
        LOW,
        AVERAGE,
        HIGH,
    }
}
