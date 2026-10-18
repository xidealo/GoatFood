package com.bunbeauty.designsystem.ui.element.switcher

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bunbeauty.designsystem.theme.FoodDeliveryTheme
import com.bunbeauty.designsystem.theme.medium
import com.bunbeauty.designsystem.ui.element.shimmer.Shimmer
import com.bunbeauty.designsystem.ui.element.switcher.FoodDeliverySwitcherDefaults.switcherShape
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun FoodDeliverySwitcher(
    optionResIdList: ImmutableList<String>,
    modifier: Modifier = Modifier,
    position: Int = 0,
    isLoading: Boolean = false,
    onPositionChanged: (Int) -> Unit,
) {
    Crossfade(
        targetState = isLoading,
        label = "FoodDeliverySwitcher",
    ) { loading ->
        if (loading) {
            Shimmer(
                modifier =
                    modifier
                        .height(48.dp)
                        .clip(FoodDeliverySwitcherDefaults.switcherButtonShape),
            )
        } else {
            FoodDeliverySwitcher(
                modifier = modifier,
                optionList = optionResIdList,
                position = position,
                onPositionChanged = onPositionChanged,
            )
        }
    }
}

@Composable
fun FoodDeliverySwitcher(
    modifier: Modifier = Modifier,
    optionList: ImmutableList<String> = persistentListOf(),
    position: Int = 0,
    onPositionChanged: (Int) -> Unit,
) {
    Row(
        modifier =
            modifier
                .background(
                    color = FoodDeliveryTheme.colors.mainColors.stroke,
                    shape = switcherShape,
                ).padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        optionList.onEachIndexed { i, text ->
            SwitcherButton(
                modifier =
                    Modifier
                        .weight(1f),
                text = text,
                enabled = position != i,
                onClick = {
                    onPositionChanged(i)
                },
            )
        }
    }
}

@Composable
private fun SwitcherButton(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier.height(40.dp),
        onClick = onClick,
        enabled = enabled,
        shape = FoodDeliverySwitcherDefaults.switcherButtonShape,
        colors = FoodDeliverySwitcherDefaults.switcherButtonColor,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = text,
                style = FoodDeliveryTheme.typography.labelLarge.medium,
            )
        }
    }
}

@Preview
@Composable
private fun SwitcherPreview() {
    FoodDeliveryTheme {
        FoodDeliverySwitcher(
            optionResIdList =
                persistentListOf(
                    "Доставка",
                    "Самовывоз",
                ),
            position = 1,
            onPositionChanged = {},
        )
    }
}

@Preview
@Composable
private fun SwitcherWithOnePreview() {
    FoodDeliveryTheme {
        FoodDeliverySwitcher(
            optionResIdList =
                persistentListOf(
                    "Доставка",
                ),
            position = 0,
            onPositionChanged = {},
        )
    }
}

@Preview
@Composable
private fun SwitcherLoadingPreview() {
    FoodDeliveryTheme {
        FoodDeliverySwitcher(
            optionResIdList =
                persistentListOf(
                    "Доставка",
                ),
            position = 0,
            onPositionChanged = {},
            isLoading = true,
        )
    }
}
