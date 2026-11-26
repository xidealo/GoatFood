package com.bunbeauty.shared.domain.feature.review

import com.bunbeauty.core.domain.review.AppReviewManager
import com.bunbeauty.core.domain.review.AppReviewSource

class NoOpAppReviewManager : AppReviewManager {
    override suspend fun request(
        orderUuid: String,
        source: AppReviewSource,
    ) = Unit
}
