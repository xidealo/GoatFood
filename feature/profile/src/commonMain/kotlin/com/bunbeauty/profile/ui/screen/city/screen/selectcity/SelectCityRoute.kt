package com.bunbeauty.profile.ui.screen.city.screen.selectcity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bunbeauty.core.model.city.City
import com.bunbeauty.designsystem.theme.FoodDeliveryTheme
import com.bunbeauty.designsystem.theme.bold
import com.bunbeauty.designsystem.ui.LocalBottomBarPadding
import com.bunbeauty.designsystem.ui.element.FoodDeliveryScaffold
import com.bunbeauty.designsystem.ui.element.button.MainButton
import com.bunbeauty.designsystem.ui.element.footer.WebSiteFooter
import com.bunbeauty.designsystem.ui.element.footer.WebStickyFooterLazyColumn
import com.bunbeauty.designsystem.ui.screen.ErrorScreen
import com.bunbeauty.designsystem.ui.screen.LoadingScreen
import com.bunbeauty.profile.presentation.selectcity.SelectCityDataState
import com.bunbeauty.profile.presentation.selectcity.SelectCityViewModel
import com.bunbeauty.profile.ui.screen.city.ui.CityItem
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import papakarlo.designsystem.generated.resources.Res
import papakarlo.designsystem.generated.resources.action_select_city_continue
import papakarlo.designsystem.generated.resources.description_select_city_location
import papakarlo.designsystem.generated.resources.error_select_city_loading
import papakarlo.designsystem.generated.resources.ic_select_city_location
import papakarlo.designsystem.generated.resources.msg_select_city_single_city_available
import papakarlo.designsystem.generated.resources.title_select_city

@Composable
private fun SelectCityDataState.DataState.mapState(): SelectCityViewState =
    SelectCityViewState(
        state =
            when (state) {
                SelectCityDataState.DataState.State.LOADING -> SelectCityViewState.State.Loading
                SelectCityDataState.DataState.State.SUCCESS -> SelectCityViewState.State.Success
                SelectCityDataState.DataState.State.ERROR -> SelectCityViewState.State.Error
            },
        cityList = cityList,
        contentMode = contentMode,
    )

@Composable
fun SelectCityRoute(
    viewModel: SelectCityViewModel = koinViewModel(),
    goToMenuFragment: () -> Unit,
) {
    LaunchedEffect(Unit) {
        viewModel.onAction(SelectCityDataState.Action.GetCityList)
    }
    val onAction =
        remember {
            { event: SelectCityDataState.Action ->
                viewModel.onAction(event)
            }
        }

    val viewState by viewModel.dataState.collectAsStateWithLifecycle()

    val effects by viewModel.events.collectAsStateWithLifecycle()
    val consumeEffects =
        remember {
            {
                viewModel.consumeEvents(effects)
            }
        }

    SelectCityEffect(
        consumeEventList = consumeEffects,
        goToMenuFragment = goToMenuFragment,
        effects = effects,
    )
    SelectCityScreen(
        viewState = viewState.mapState(),
        onAction = onAction,
    )
}

@Composable
private fun SelectCityScreen(
    viewState: SelectCityViewState,
    onAction: (SelectCityDataState.Action) -> Unit,
) {
    FoodDeliveryScaffold(title = stringResource(Res.string.title_select_city)) {
        when (viewState.state) {
            SelectCityViewState.State.Loading -> {
                LoadingScreen()
            }

            SelectCityViewState.State.Success -> {
                SelectCitySuccessScreen(
                    viewState = viewState,
                    onAction = onAction,
                )
            }

            SelectCityViewState.State.Error -> {
                ErrorScreen(
                    mainTextId = Res.string.error_select_city_loading,
                    onClick = { onAction(SelectCityDataState.Action.OnRefreshClicked) },
                )
            }
        }
    }
}

@Composable
private fun SelectCitySuccessScreen(
    viewState: SelectCityViewState,
    onAction: (SelectCityDataState.Action) -> Unit,
) {
    when (viewState.contentMode) {
        SelectCityDataState.DataState.ContentMode.CityList -> {
            SelectCityListScreen(
                viewState = viewState,
                onAction = onAction,
            )
        }

        SelectCityDataState.DataState.ContentMode.SingleCity -> {
            val city = viewState.cityList.firstOrNull()
            if (city == null) {
                ErrorScreen(
                    mainTextId = Res.string.error_select_city_loading,
                    onClick = { onAction(SelectCityDataState.Action.OnRefreshClicked) },
                )
            } else {
                SelectCitySingleCityScreen(
                    city = city,
                    onAction = onAction,
                )
            }
        }
    }
}

@Composable
private fun SelectCityListScreen(
    viewState: SelectCityViewState,
    onAction: (SelectCityDataState.Action) -> Unit,
) {
    WebStickyFooterLazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = spacedBy(8.dp),
        contentPadding = PaddingValues(FoodDeliveryTheme.dimensions.mediumSpace),
    ) {
        items(viewState.cityList) { city ->
            CityItem(
                cityName = city.name,
                onClick = {
                    onAction(SelectCityDataState.Action.OnCitySelected(city))
                },
            )
        }
    }
}

@Composable
private fun SelectCitySingleCityScreen(
    city: City,
    onAction: (SelectCityDataState.Action) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(color = FoodDeliveryTheme.colors.mainColors.background),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(weight = 1f))

        Box(
            modifier =
                Modifier
                    .size(size = 120.dp)
                    .clip(CircleShape)
                    .background(color = FoodDeliveryTheme.colors.statusColors.warning),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                modifier = Modifier.size(size = 64.dp),
                painter = painterResource(resource = Res.drawable.ic_select_city_location),
                tint = FoodDeliveryTheme.colors.statusColors.onStatus,
                contentDescription = stringResource(resource = Res.string.description_select_city_location),
            )
        }

        Text(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
                    .padding(horizontal = 16.dp),
            text = stringResource(resource = Res.string.msg_select_city_single_city_available),
            style = FoodDeliveryTheme.typography.bodyLarge,
            color = FoodDeliveryTheme.colors.mainColors.onSurface,
            textAlign = TextAlign.Center,
        )

        Text(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .padding(horizontal = 16.dp),
            text = city.name,
            style = FoodDeliveryTheme.typography.titleLarge.bold,
            color = FoodDeliveryTheme.colors.mainColors.onSurface,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.weight(weight = 1f))

        WebSiteFooter()

        MainButton(
            modifier =
                Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = LocalBottomBarPadding.current + 16.dp),
            textStringId = Res.string.action_select_city_continue,
        ) {
            onAction(SelectCityDataState.Action.OnContinueClicked)
        }
    }
}

@Composable
private fun SelectCityEffect(
    effects: List<SelectCityDataState.Event>,
    goToMenuFragment: () -> Unit,
    consumeEventList: () -> Unit,
) {
    LaunchedEffect(effects) {
        effects.forEach { event ->
            when (event) {
                SelectCityDataState.Event.NavigateToMenu -> goToMenuFragment()
            }
            consumeEventList()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectCitySuccessScreenPreview() {
    FoodDeliveryTheme {
        SelectCityScreen(
            viewState =
                SelectCityViewState(
                    state = SelectCityViewState.State.Success,
                    cityList =
                        listOf(
                            City(
                                uuid = "123",
                                name = "Кимры",
                                timeZone = "",
                            ),
                            City(
                                uuid = "456",
                                name = "Москва",
                                timeZone = "",
                            ),
                        ),
                ),
            onAction = { },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectCitySingleCityScreenPreview() {
    FoodDeliveryTheme {
        SelectCityScreen(
            viewState =
                SelectCityViewState(
                    state = SelectCityViewState.State.Success,
                    contentMode = SelectCityDataState.DataState.ContentMode.SingleCity,
                    cityList =
                        listOf(
                            City(
                                uuid = "123",
                                name = "Кимры",
                                timeZone = "",
                            ),
                        ),
                ),
            onAction = { },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectCityLoadingScreenPreview() {
    FoodDeliveryTheme {
        SelectCityScreen(
            viewState =
                SelectCityViewState(
                    state = SelectCityViewState.State.Loading,
                    cityList =
                        listOf(
                            City(
                                uuid = "123",
                                name = "Кимры",
                                timeZone = "",
                            ),
                            City(
                                uuid = "456",
                                name = "Москва",
                                timeZone = "",
                            ),
                        ),
                ),
            onAction = { },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectCityErrorScreenPreview() {
    FoodDeliveryTheme {
        SelectCityScreen(
            viewState =
                SelectCityViewState(
                    state = SelectCityViewState.State.Error,
                    cityList =
                        listOf(
                            City(
                                uuid = "123",
                                name = "Кимры",
                                timeZone = "",
                            ),
                        ),
                ),
            onAction = { },
        )
    }
}
