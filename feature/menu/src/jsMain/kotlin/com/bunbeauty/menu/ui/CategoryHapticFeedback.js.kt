package com.bunbeauty.menu.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.platform.LocalHapticFeedback

@Composable
internal actual fun rememberCategoryHapticFeedback(): HapticFeedback = LocalHapticFeedback.current
