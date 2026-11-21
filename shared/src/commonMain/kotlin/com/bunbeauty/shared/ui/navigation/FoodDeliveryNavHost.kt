package com.bunbeauty.shared.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.bunbeauty.shared.ui.navigation.splash.SplashScreenDestination

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun FoodDeliveryNavHost(
    navController: NavHostController,
    showInfoMessage: (String, Int) -> Unit,
    showErrorMessage: (String) -> Unit,
    onReady: () -> Unit = {},
) {
    NavHost(
        navController = navController,
        startDestination = SplashScreenDestination,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {
        foodDeliveryNavGraphBuilder(
            navController = navController,
            showErrorMessage = showErrorMessage,
            showInfoMessage = showInfoMessage,
        )
    }

    LaunchedEffect(navController) {
        navController.bindSystemBackNavigation()
        onReady()
    }
}
