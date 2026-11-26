package com.bunbeauty.menu.ui

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalView

@Composable
internal actual fun rememberCategoryHapticFeedback(): HapticFeedback {
    val view = LocalView.current
    return remember(view) {
        object : HapticFeedback {
            override fun performHapticFeedback(hapticFeedbackType: HapticFeedbackType) {
                view.performHapticFeedback(
                    hapticFeedbackType.toPlatformConstant(),
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING,
                )
            }
        }
    }
}

private fun HapticFeedbackType.toPlatformConstant(): Int = when (this) {
    HapticFeedbackType.SegmentTick ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            HapticFeedbackConstants.SEGMENT_TICK
        } else {
            HapticFeedbackConstants.CONTEXT_CLICK
        }
    else -> HapticFeedbackConstants.CONTEXT_CLICK
}
