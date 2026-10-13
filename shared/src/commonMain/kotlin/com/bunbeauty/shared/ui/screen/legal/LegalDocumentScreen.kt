package com.bunbeauty.shared.ui.screen.legal

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bunbeauty.designsystem.theme.FoodDeliveryTheme
import com.bunbeauty.designsystem.ui.element.FoodDeliveryScaffold
import com.bunbeauty.designsystem.ui.element.footer.WebStickyFooterColumn

@Composable
fun LegalDocumentScreen(
    title: String,
    text: String,
    back: () -> Unit,
) {
    FoodDeliveryScaffold(
        title = title,
        backActionClick = back,
    ) {
        WebStickyFooterColumn {
            Text(
                modifier = Modifier.padding(16.dp),
                text = text,
                style = FoodDeliveryTheme.typography.bodyMedium,
                color = FoodDeliveryTheme.colors.mainColors.onSurface,
            )
        }
    }
}
