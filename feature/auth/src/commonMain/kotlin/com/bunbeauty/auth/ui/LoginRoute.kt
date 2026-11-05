package com.bunbeauty.auth.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bunbeauty.auth.presentation.login.Login
import com.bunbeauty.auth.presentation.login.LoginViewModel
import com.bunbeauty.core.model.SuccessLoginDirection
import com.bunbeauty.designsystem.theme.FoodDeliveryTheme
import com.bunbeauty.designsystem.theme.logoMedium
import com.bunbeauty.designsystem.ui.LocalBottomBarPadding
import com.bunbeauty.designsystem.ui.element.FoodDeliveryScaffold
import com.bunbeauty.designsystem.ui.element.button.LoadingButton
import com.bunbeauty.designsystem.ui.element.footer.WebSiteFooter
import com.bunbeauty.designsystem.ui.element.textfield.FoodDeliveryTextField
import com.bunbeauty.designsystem.ui.element.textfield.FoodDeliveryTextFieldDefaults
import com.bunbeauty.designsystem.ui.getIsImeVisible
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import papakarlo.designsystem.generated.resources.Res
import papakarlo.designsystem.generated.resources.action_login_continue
import papakarlo.designsystem.generated.resources.description_login_logo
import papakarlo.designsystem.generated.resources.error_login_phone
import papakarlo.designsystem.generated.resources.error_login_too_many_requests
import papakarlo.designsystem.generated.resources.error_something_went_wrong
import papakarlo.designsystem.generated.resources.hint_login_phone
import papakarlo.designsystem.generated.resources.msg_login_info

private val logoBoxHeightLimit: Dp = 200.dp

@Composable
fun LoginRoute(
    viewModel: LoginViewModel = koinViewModel(),
    successLoginDirection: SuccessLoginDirection,
    back: () -> Unit,
    goToConfirm: (phoneNumber: String, successLoginDirection: SuccessLoginDirection) -> Unit,
    showErrorMessage: (String) -> Unit,
) {
    LaunchedEffect(Unit) {
        viewModel.onAction(Login.Action.Init)
    }
    val viewState by viewModel.dataState.collectAsStateWithLifecycle()

    val onAction =
        remember {
            { event: Login.Action ->
                viewModel.onAction(event)
            }
        }

    val effects by viewModel.events.collectAsStateWithLifecycle()
    val consumeEffects =
        remember {
            {
                viewModel.consumeEvents(effects)
            }
        }

    LoginEffect(
        effects = effects,
        back = back,
        successLoginDirection = successLoginDirection,
        goToConfirm = goToConfirm,
        consumeEffects = consumeEffects,
        showErrorMessage = showErrorMessage,
    )
    LoginScreen(viewState = viewState, onAction = onAction)
}

@Composable
private fun LoginScreen(
    viewState: Login.ViewDataState,
    onAction: (Login.Action) -> Unit,
) {
    FoodDeliveryScaffold(
        backActionClick = {
            onAction(Login.Action.BackClick)
        },
        backgroundColor = FoodDeliveryTheme.colors.mainColors.surface,
        actionButton = {
            LoadingButton(
                modifier =
                    Modifier
                        .padding(horizontal = FoodDeliveryTheme.dimensions.mediumSpace)
                        .padding(
                            bottom =
                                if (getIsImeVisible()) {
                                    0.dp
                                } else {
                                    LocalBottomBarPadding.current
                                },
                        ),
                textStringId = Res.string.action_login_continue,
                isLoading = viewState.isLoading,
                onClick = {
                    onAction(Login.Action.NextClick)
                },
            )
        },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(FoodDeliveryTheme.dimensions.mediumSpace),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                BoxWithConstraints {
                    val constraints = this
                    if (constraints.maxHeight > logoBoxHeightLimit) {
                        logoMedium?.let { logo ->
                            Image(
                                modifier = Modifier.height(height = 156.dp),
                                painter = painterResource(resource = logo),
                                contentDescription = stringResource(resource = Res.string.description_login_logo),
                            )
                        }
                    }
                }
                Text(
                    modifier = Modifier.padding(top = FoodDeliveryTheme.dimensions.mediumSpace),
                    text = stringResource(Res.string.msg_login_info),
                    style = FoodDeliveryTheme.typography.bodyLarge,
                    color = FoodDeliveryTheme.colors.mainColors.onSurface,
                )

                val focusRequester =
                    remember {
                        FocusRequester()
                    }
                FoodDeliveryTextField(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = FoodDeliveryTheme.dimensions.mediumSpace),
                    focusRequester = focusRequester,
                    value =
                        TextFieldValue(
                            text = viewState.phoneNumber,
                            selection = TextRange(viewState.phoneNumberCursorPosition),
                        ),
                    labelStringId = Res.string.hint_login_phone,
                    keyboardOptions =
                        FoodDeliveryTextFieldDefaults.keyboardOptionsDefault(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Done,
                        ),
                    onValueChange = { value ->
                        onAction(Login.Action.ChangePhoneNumber(value.text, value.selection.start))
                    },
                    errorMessageId =
                        if (viewState.hasPhoneError) {
                            Res.string.error_login_phone
                        } else {
                            null
                        },
                )

                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            }

            WebSiteFooter(
                modifier =
                    Modifier.padding(
                        bottom = FoodDeliveryTheme.dimensions.scrollScreenBottomSpace,
                    ),
            )
        }
    }
}

@Composable
private fun LoginEffect(
    effects: List<Login.Event>,
    successLoginDirection: SuccessLoginDirection,
    back: () -> Unit,
    goToConfirm: (phoneNumber: String, successLoginDirection: SuccessLoginDirection) -> Unit,
    consumeEffects: () -> Unit,
    showErrorMessage: (String) -> Unit,
) {
    LaunchedEffect(effects) {
        effects.forEach { effect ->
            when (effect) {
                is Login.Event.NavigateToConfirm -> {
                    goToConfirm(
                        effect.phoneNumber,
                        successLoginDirection,
                    )
                }

                Login.Event.ShowTooManyRequestsError -> {
                    showErrorMessage(getString(Res.string.error_login_too_many_requests))
                }

                Login.Event.ShowSomethingWentWrongError -> {
                    showErrorMessage(getString(Res.string.error_something_went_wrong))
                }

                Login.Event.NavigateBack -> back()
            }
        }
        consumeEffects()
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    FoodDeliveryTheme {
        LoginScreen(
            viewState =
                Login.ViewDataState(
                    phoneNumber = "+7 (900) 900-90-90",
                    phoneNumberCursorPosition = 18,
                    hasPhoneError = false,
                    isLoading = false,
                ),
            onAction = {},
        )
    }
}
