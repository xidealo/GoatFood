package com.bunbeauty.core.domain.cafe

import com.bunbeauty.core.Constants.SECONDS_IN_HOUR
import com.bunbeauty.core.Constants.SECONDS_IN_MINUTE
import com.bunbeauty.core.Constants.TIME_DIVIDER
import com.bunbeauty.core.domain.repo.CafeRepo
import com.bunbeauty.core.domain.repo.MenuProductRepo
import com.bunbeauty.core.model.cafe.Cafe

class CafeInteractor(
    private val cafeRepo: CafeRepo,
    private val menuProductRepo: MenuProductRepo,
) : ICafeInteractor {
    override suspend fun getCafeByUuid(cafeUuid: String): Cafe? = cafeRepo.getCafeByUuid(cafeUuid)

    override suspend fun saveSelectedCafe(cafeUuid: String) {
        cafeRepo.saveSelectedCafeUuid(cafeUuid)
        menuProductRepo.clearCache()
    }

    override fun getCafeTime(daySeconds: Int): String {
        val hours = daySeconds / SECONDS_IN_HOUR
        val minutes = (daySeconds % SECONDS_IN_HOUR) / SECONDS_IN_MINUTE
        val minutesString =
            if (minutes < 10) {
                "0$minutes"
            } else {
                minutes.toString()
            }

        return "$hours$TIME_DIVIDER$minutesString"
    }
}
