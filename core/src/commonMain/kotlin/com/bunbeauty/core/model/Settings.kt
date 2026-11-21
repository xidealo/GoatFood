package com.bunbeauty.core.model

class Settings(
    val userUuid: String,
    val phoneNumber: String,
    val email: String?,
    val personalDiscountPercent: Int? = null,
    val marketingNotificationsEnabled: Boolean = true,
    val systemNotificationsEnabled: Boolean = false,
)
