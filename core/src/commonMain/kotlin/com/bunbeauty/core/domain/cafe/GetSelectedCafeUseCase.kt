package com.bunbeauty.core.domain.cafe

import com.bunbeauty.core.domain.exeptions.EmptyCafeListException
import com.bunbeauty.core.domain.repo.CafeRepo
import com.bunbeauty.core.model.cafe.Cafe

interface GetSelectedCafeUseCase {
    suspend operator fun invoke(): Cafe
}

class GetSelectedCafeUseCaseImpl(
    private val cafeRepo: CafeRepo,
    private val getCafeListUseCase: GetCafeListUseCase,
    private val isPickupEnabledFromCafeUseCase: IsPickupEnabledFromCafeUseCase,
) : GetSelectedCafeUseCase {
    override suspend operator fun invoke(): Cafe {
        val cafeList = getCafeListUseCase().ifEmpty { throw EmptyCafeListException() }
        return cafeRepo.getSelectedCafeByUserAndCityUuid()
            ?: cafeList.firstOrNull { cafe ->
                isPickupEnabledFromCafeUseCase(cafeUuid = cafe.uuid)
            }
            ?: cafeList.first()
    }
}
