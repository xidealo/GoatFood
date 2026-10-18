package com.bunbeauty.designsystem.theme

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import com.bunbeauty.designsystem.FoodDeliveryCompany
import com.bunbeauty.designsystem.theme.color.AppColors
import com.bunbeauty.designsystem.theme.color.BeregColors
import com.bunbeauty.designsystem.theme.color.BeregDarkColors
import com.bunbeauty.designsystem.theme.color.DjanDarkColors
import com.bunbeauty.designsystem.theme.color.DjanLightColors
import com.bunbeauty.designsystem.theme.color.EmojiCenterDarkColors
import com.bunbeauty.designsystem.theme.color.EmojiCenterLightColors
import com.bunbeauty.designsystem.theme.color.EmojiDarkColors
import com.bunbeauty.designsystem.theme.color.EmojiLightColors
import com.bunbeauty.designsystem.theme.color.EstPoestDarkColors
import com.bunbeauty.designsystem.theme.color.EstPoestLightColors
import com.bunbeauty.designsystem.theme.color.GustoPubDarkColors
import com.bunbeauty.designsystem.theme.color.GustoPubLightColors
import com.bunbeauty.designsystem.theme.color.LegendaDarkColors
import com.bunbeauty.designsystem.theme.color.LegendaLightColors
import com.bunbeauty.designsystem.theme.color.VostochkaColors
import com.bunbeauty.designsystem.theme.color.VostochkaDarkColors
import com.bunbeauty.designsystem.theme.color.MiminoDarkColors
import com.bunbeauty.designsystem.theme.color.MiminoLightColors
import com.bunbeauty.designsystem.theme.color.LocalAppColors
import com.bunbeauty.designsystem.theme.color.PapaKarloDarkColors
import com.bunbeauty.designsystem.theme.color.PapaKarloLightColors
import com.bunbeauty.designsystem.theme.color.TandirHouseDarkColors
import com.bunbeauty.designsystem.theme.color.TandirHouseLightColors
import com.bunbeauty.designsystem.theme.color.TavernaColors
import com.bunbeauty.designsystem.theme.color.TavernaDarkColors
import com.bunbeauty.designsystem.theme.color.UsadbaDarkColors
import com.bunbeauty.designsystem.theme.color.UsadbaLightColors
import com.bunbeauty.designsystem.theme.color.VkusKavkazaDarkColors
import com.bunbeauty.designsystem.theme.color.VkusKavkazaLightColors
import com.bunbeauty.designsystem.theme.color.VoljaneColors
import com.bunbeauty.designsystem.theme.color.VoljaneDarkColors
import com.bunbeauty.designsystem.theme.color.YuliarDarkColors
import com.bunbeauty.designsystem.theme.color.YuliarLightColors
import org.jetbrains.compose.resources.DrawableResource
import papakarlo.designsystem.generated.resources.Res
import papakarlo.designsystem.generated.resources.logo_medium_bereg
import papakarlo.designsystem.generated.resources.logo_medium_djan
import papakarlo.designsystem.generated.resources.logo_medium_mimino_dark
import papakarlo.designsystem.generated.resources.logo_medium_mimino_light
import papakarlo.designsystem.generated.resources.logo_medium_emojicenter_dark
import papakarlo.designsystem.generated.resources.logo_medium_emojicenter_light
import papakarlo.designsystem.generated.resources.logo_medium_emoji
import papakarlo.designsystem.generated.resources.logo_medium_estpoest
import papakarlo.designsystem.generated.resources.logo_medium_gustopub
import papakarlo.designsystem.generated.resources.logo_medium_legenda
import papakarlo.designsystem.generated.resources.logo_medium_papakarlo
import papakarlo.designsystem.generated.resources.logo_medium_tandir_house
import papakarlo.designsystem.generated.resources.logo_medium_taverna
import papakarlo.designsystem.generated.resources.logo_medium_usadba
import papakarlo.designsystem.generated.resources.logo_medium_vkus_kavkaza
import papakarlo.designsystem.generated.resources.logo_medium_voljane
import papakarlo.designsystem.generated.resources.logo_medium_yuliar
import papakarlo.designsystem.generated.resources.logo_small_emojicenter_dark
import papakarlo.designsystem.generated.resources.logo_small_emojicenter_light
import papakarlo.designsystem.generated.resources.logo_small_mimino_dark
import papakarlo.designsystem.generated.resources.logo_small_mimino_light

private var BASE_THEME_FLAVOR: String = "papakarlo"
var logoMedium: DrawableResource? = null
var logoSmall: DrawableResource? = null

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FoodDeliveryTheme(
    flavor: String = BASE_THEME_FLAVOR,
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = getAppColors(
        isDarkTheme = isDarkTheme,
        flavor = flavor
    )
    val rememberedColors = remember {
        colors.copy()
    }.apply {
        update(colors)
    }

    CompositionLocalProvider(
        LocalAppColors provides rememberedColors,
        LocalAppDimensions provides AppDimensions(),
        LocalAppTypography provides AppTypography(),
        content = content,
    )
}

private fun getAppColors(
    flavor: String,
    isDarkTheme: Boolean,
): AppColors {
    logoSmall = null
    val company = FoodDeliveryCompany.getByFlavor(flavor)
    return when (company) {
        FoodDeliveryCompany.PAPA_KARLO -> {
            logoMedium = Res.drawable.logo_medium_papakarlo
            if (isDarkTheme) {
                PapaKarloDarkColors
            } else {
                PapaKarloLightColors
            }
        }

        FoodDeliveryCompany.YULIAR -> {
            logoMedium = Res.drawable.logo_medium_yuliar
            if (isDarkTheme) {
                YuliarDarkColors
            } else {
                YuliarLightColors
            }
        }

        FoodDeliveryCompany.DJAN -> {
            logoMedium = Res.drawable.logo_medium_djan
            if (isDarkTheme) {
                DjanDarkColors
            } else {
                DjanLightColors
            }
        }

        FoodDeliveryCompany.GUSTO_PUB -> {
            logoMedium = Res.drawable.logo_medium_gustopub
            if (isDarkTheme) {
                GustoPubDarkColors
            } else {
                GustoPubLightColors
            }
        }

        FoodDeliveryCompany.TANDIR_HOUSE -> {
            logoMedium = Res.drawable.logo_medium_tandir_house
            if (isDarkTheme) {
                TandirHouseDarkColors
            } else {
                TandirHouseLightColors
            }
        }

        FoodDeliveryCompany.VKUS_KAVKAZA -> {
            logoMedium = Res.drawable.logo_medium_vkus_kavkaza
            if (isDarkTheme) {
                VkusKavkazaDarkColors
            } else {
                VkusKavkazaLightColors
            }
        }

        FoodDeliveryCompany.EST_POEST -> {
            logoMedium = Res.drawable.logo_medium_estpoest
            if (isDarkTheme) {
                EstPoestDarkColors
            } else {
                EstPoestLightColors
            }
        }

        FoodDeliveryCompany.LEGENDA -> {
            logoMedium = Res.drawable.logo_medium_legenda

            if (isDarkTheme) {
                LegendaDarkColors
            } else {
                LegendaLightColors
            }
        }

        FoodDeliveryCompany.USADBA -> {
            logoMedium = Res.drawable.logo_medium_usadba
            if (isDarkTheme) {
                UsadbaDarkColors
            } else {
                UsadbaLightColors
            }
        }

        FoodDeliveryCompany.EMOJI -> {
            logoMedium = Res.drawable.logo_medium_emoji
            if (isDarkTheme) {
                EmojiDarkColors
            } else {
                EmojiLightColors
            }
        }

        FoodDeliveryCompany.VOSTOCHKA ->{
            logoMedium = Res.drawable.logo_medium_taverna

            if (isDarkTheme) {
                VostochkaDarkColors
            } else {
                VostochkaColors
            }
        }

        FoodDeliveryCompany.TAVERNA -> {
            logoMedium = Res.drawable.logo_medium_taverna
            if (isDarkTheme) {
                TavernaDarkColors
            } else {
                TavernaColors
            }
        }

        FoodDeliveryCompany.VOLJANE -> {
            logoMedium = Res.drawable.logo_medium_voljane
            if (isDarkTheme) {
                VoljaneDarkColors
            } else {
                VoljaneColors
            }
        }

        FoodDeliveryCompany.BEREG -> {
            logoMedium = Res.drawable.logo_medium_bereg
            if (isDarkTheme) {
                BeregDarkColors
            } else {
                BeregColors
            }
        }

        FoodDeliveryCompany.MIMINO -> {
            logoMedium =
                if (isDarkTheme) {
                    Res.drawable.logo_medium_mimino_dark
                } else {
                    Res.drawable.logo_medium_mimino_light
                }
            logoSmall =
                if (isDarkTheme) {
                    Res.drawable.logo_small_mimino_dark
                } else {
                    Res.drawable.logo_small_mimino_light
                }
            if (isDarkTheme) {
                MiminoDarkColors
            } else {
                MiminoLightColors
            }
        }

        FoodDeliveryCompany.EMOJI_CENTER -> {
            logoMedium =
                if (isDarkTheme) {
                    Res.drawable.logo_medium_emojicenter_dark
                } else {
                    Res.drawable.logo_medium_emojicenter_light
                }
            logoSmall =
                if (isDarkTheme) {
                    Res.drawable.logo_small_emojicenter_dark
                } else {
                    Res.drawable.logo_small_emojicenter_light
                }
            if (isDarkTheme) {
                EmojiCenterDarkColors
            } else {
                EmojiCenterLightColors
            }
        }
    }
}

object FoodDeliveryTheme {
    val colors: AppColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current
    val typography: AppTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalAppTypography.current
    val dimensions: AppDimensions
        @Composable
        @ReadOnlyComposable
        get() = LocalAppDimensions.current
}
