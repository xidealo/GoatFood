package com.bunbeauty.designsystem.theme.color

import androidx.compose.ui.graphics.Color
import com.bunbeauty.designsystem.theme.color.FoodDeliveryColors.LightBlue

private val MoroshkaPrimary = Color(0xFF4F5D08)
private val MoroshkaSurfaceVariant = Color(0xFFF1F3E6)
private val MoroshkaStrokeVariant = Color(0xFFC5CBA0)

val MoroshkaLightColors =
    AppColors(
        mainColors =
            ColorDefaults.lightMainColors(
                primary = MoroshkaPrimary,
                surfaceVariant = MoroshkaSurfaceVariant,
                strokeVariant = MoroshkaStrokeVariant,
            ),
        orderColors = ColorDefaults.orderColors(),
        statusColors = ColorDefaults.statusColors(),
        bunBeautyBrandColor = LightBlue,
        isLight = true,
    )

val MoroshkaDarkColors =
    AppColors(
        mainColors =
            ColorDefaults.darkMainColors(
                primary = MoroshkaPrimary,
            ),
        orderColors = ColorDefaults.orderColors(),
        statusColors = ColorDefaults.statusColors(),
        bunBeautyBrandColor = LightBlue,
        isLight = false,
    )
