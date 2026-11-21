package com.bunbeauty.profile.ui.screen.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.bunbeauty.designsystem.ui.element.button.MainButton
import com.bunbeauty.designsystem.ui.element.textfield.FoodDeliveryTextField
import com.bunbeauty.designsystem.ui.screen.bottomsheet.FoodDeliveryModalBottomSheet
import com.bunbeauty.profile.presentation.settings.SettingsState
import org.jetbrains.compose.resources.stringResource
import papakarlo.designsystem.generated.resources.Res
import papakarlo.designsystem.generated.resources.action_settings_save
import papakarlo.designsystem.generated.resources.hint_settings_email
import papakarlo.designsystem.generated.resources.title_settings_email

@Composable
fun EmailBottomSheetScreen(
    isShown: Boolean,
    emailInput: String,
    onAction: (SettingsState.Action) -> Unit,
) {
    FoodDeliveryModalBottomSheet(
        onDismissRequest = {
            onAction(SettingsState.Action.CloseEmailBottomSheet)
        },
        isShown = isShown,
        title = stringResource(Res.string.title_settings_email),
    ) {
        FoodDeliveryTextField(
            modifier = Modifier.padding(bottom = 16.dp),
            value = emailInput,
            labelStringId = Res.string.hint_settings_email,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            onValueChange = { value ->
                onAction(SettingsState.Action.OnEmailChanged(value))
            },
        )
        MainButton(
            textStringId = Res.string.action_settings_save,
            elevated = false,
            onClick = {
                onAction(SettingsState.Action.SaveEmail)
            },
        )
    }
}
