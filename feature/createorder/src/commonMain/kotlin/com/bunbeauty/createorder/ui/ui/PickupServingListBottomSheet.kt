package com.bunbeauty.createorder.ui.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.bunbeauty.core.model.order.PickupServing
import com.bunbeauty.createorder.presentation.createorder.CreateOrder
import com.bunbeauty.createorder.ui.PickupServingListUI
import com.bunbeauty.createorder.ui.SelectablePickupServingUI
import com.bunbeauty.designsystem.ui.element.selectable.SelectableItem
import com.bunbeauty.designsystem.ui.screen.bottomsheet.FoodDeliveryModalBottomSheet
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import papakarlo.designsystem.generated.resources.Res
import papakarlo.designsystem.generated.resources.pickup_serving

@Composable
fun PickupServingListBottomSheet(
    pickupServingList: PickupServingListUI,
    onAction: (CreateOrder.Action) -> Unit,
) {
    FoodDeliveryModalBottomSheet(
        onDismissRequest = {
            onAction(CreateOrder.Action.HidePickupServingList)
        },
        isShown = pickupServingList.isShown,
        title = stringResource(Res.string.pickup_serving),
    ) {
        PickupServingListBottomSheetContent(
            pickupServingList = pickupServingList.pickupServingList,
            onAction = onAction,
        )
    }
}

@Composable
private fun PickupServingListBottomSheetContent(
    pickupServingList: ImmutableList<SelectablePickupServingUI>,
    onAction: (CreateOrder.Action) -> Unit,
) {
    LazyColumn(verticalArrangement = Arrangement.Absolute.spacedBy(8.dp)) {
        items(
            items = pickupServingList,
            key = { selectablePickupServing ->
                "PickupServing ${selectablePickupServing.pickupServing.name}"
            },
        ) { selectablePickupServing ->
            SelectableItem(
                title = selectablePickupServing.name,
                clickable = true,
                elevated = false,
                isSelected = selectablePickupServing.isSelected,
                onClick = {
                    onAction(
                        CreateOrder.Action.ChangePickupServing(
                            pickupServing = selectablePickupServing.pickupServing,
                        ),
                    )
                },
                enabled = true,
            )
        }
    }
}

@Preview
@Composable
private fun PickupServingListBottomSheetPreview() {
    PickupServingListBottomSheetContent(
        pickupServingList =
            persistentListOf(
                SelectablePickupServingUI(
                    pickupServing = PickupServing.TRAY,
                    name = "На подносе",
                    isSelected = false,
                ),
                SelectablePickupServingUI(
                    pickupServing = PickupServing.BAG,
                    name = "В пакете",
                    isSelected = true,
                ),
            ),
        onAction = {},
    )
}
