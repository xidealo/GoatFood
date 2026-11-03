package com.bunbeauty.designsystem.theme.color

import androidx.compose.ui.graphics.Color
import com.bunbeauty.designsystem.theme.color.FoodDeliveryColors.LightBlue

private val UrbanVibePrimary = Color(0xFFC1622B)
private val UrbanVibeSurfaceVariant = Color(0xFFF6EEDF)
private val UrbanVibeStrokeVariant = Color(0xFFE4C4A0)

val UrbanVibeLightColors =
    AppColors(
        mainColors =
            ColorDefaults.lightMainColors(
                primary = UrbanVibePrimary,
                surfaceVariant = UrbanVibeSurfaceVariant,
                strokeVariant = UrbanVibeStrokeVariant,
            ),
        orderColors = ColorDefaults.orderColors(),
        statusColors = ColorDefaults.statusColors(),
        bunBeautyBrandColor = LightBlue,
        isLight = true,
    )

val UrbanVibeDarkColors =
    AppColors(
        mainColors =
            ColorDefaults.darkMainColors(
                primary = UrbanVibePrimary,
            ),
        orderColors = ColorDefaults.orderColors(),
        statusColors = ColorDefaults.statusColors(),
        bunBeautyBrandColor = LightBlue,
        isLight = false,
    )
