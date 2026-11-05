package com.bunbeauty.designsystem.ui.element.footer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.bunbeauty.designsystem.theme.FoodDeliveryTheme
import com.bunbeauty.designsystem.ui.element.FoodDeliveryHorizontalDivider
import org.jetbrains.compose.resources.stringResource
import papakarlo.designsystem.generated.resources.Res
import papakarlo.designsystem.generated.resources.msg_footer_copyright
import papakarlo.designsystem.generated.resources.title_privacy_policy
import papakarlo.designsystem.generated.resources.title_terms_of_service
import papakarlo.designsystem.generated.resources.title_user_agreement

@Composable
fun SiteFooter(
    onUserAgreementClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onTermsOfServiceClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        FoodDeliveryHorizontalDivider()

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(FoodDeliveryTheme.colors.mainColors.surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FooterLink(
                    text = stringResource(Res.string.title_user_agreement),
                    onClick = onUserAgreementClick,
                )
                FooterLink(
                    text = stringResource(Res.string.title_privacy_policy),
                    onClick = onPrivacyPolicyClick,
                )
                FooterLink(
                    text = stringResource(Res.string.title_terms_of_service),
                    onClick = onTermsOfServiceClick,
                )
            }

            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = stringResource(Res.string.msg_footer_copyright),
                style = FoodDeliveryTheme.typography.labelSmall,
                color = FoodDeliveryTheme.colors.mainColors.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun FooterLink(
    text: String,
    onClick: () -> Unit,
) {
    Text(
        modifier = Modifier.clickable(onClick = onClick),
        text = text,
        style = FoodDeliveryTheme.typography.bodySmall,
        color = FoodDeliveryTheme.colors.mainColors.primary,
        textDecoration = TextDecoration.Underline,
    )
}
