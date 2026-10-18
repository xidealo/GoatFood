package com.bunbeauty.designsystem.theme.color

import androidx.compose.ui.graphics.Color
import com.bunbeauty.designsystem.theme.color.FoodDeliveryColors.LightBlue

private val VostochkaCenterPrimary = Color(0xFFD0021B)
private val VostochkaCenterSurfaceVariant = Color(0xFFE8E8F5)
private val VostochkaCenterStrokeVariant = Color(0xFFD0D0E8)

val VostochkaColors =
    AppColors(
        mainColors =
            ColorDefaults.lightMainColors(
                primary = VostochkaCenterPrimary,
                surfaceVariant = VostochkaCenterSurfaceVariant,
                strokeVariant = VostochkaCenterStrokeVariant,
            ),
        orderColors = ColorDefaults.orderColors(),
        statusColors = ColorDefaults.statusColors(),
        bunBeautyBrandColor = LightBlue,
        isLight = true,
    )

val VostochkaDarkColors =
    AppColors(
        mainColors =
            ColorDefaults.darkMainColors(
                primary = VostochkaCenterPrimary,
            ),
        orderColors = ColorDefaults.orderColors(),
        statusColors = ColorDefaults.statusColors(),
        bunBeautyBrandColor = LightBlue,
        isLight = false,
    )
