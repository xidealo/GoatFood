package com.bunbeauty.core.domain.review

interface AppReviewManager {
    suspend fun request(
        orderUuid: String,
        source: AppReviewSource,
    )
}

enum class AppReviewSource {
    MENU,
    ORDER_DETAILS,
}
