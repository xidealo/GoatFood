package com.bunbeauty.core.domain.cafe

import com.bunbeauty.core.model.cafe.SelectableCafe

class GetSelectableCafeListUseCase(
    private val getCafeListUseCase: GetCafeListUseCase,
    private val getSelectedCafeUseCase: GetSelectedCafeUseCase,
    private val isPickupEnabledFromCafeUseCaseImpl: IsPickupEnabledFromCafeUseCase,
) {
    suspend operator fun invoke(): List<SelectableCafe> {
        val selectedCafe = getSelectedCafeUseCase()

        return getCafeListUseCase()
            .map { cafe ->
                SelectableCafe(
                    cafe = cafe,
                    isSelected = cafe.uuid == selectedCafe.uuid,
                    canBePickup = isPickupEnabledFromCafeUseCaseImpl(cafeUuid = cafe.uuid),
                )
            }.sortedByDescending { selectableCafe ->
                selectableCafe.canBePickup
            }
    }
}
