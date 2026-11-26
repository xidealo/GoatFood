package com.bunbeauty.papakarlo.feature.main

import android.Manifest
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import com.bunbeauty.core.domain.repo.UserRepo
import com.bunbeauty.designsystem.theme.FoodDeliveryTheme
import com.bunbeauty.papakarlo.BuildConfig
import com.bunbeauty.papakarlo.R
import com.bunbeauty.shared.domain.feature.notification.NotificationNavigation
import com.bunbeauty.shared.domain.feature.notification.NotificationOpen
import com.bunbeauty.shared.domain.feature.notification.NotificationPermissionRequester
import com.bunbeauty.shared.domain.feature.review.AndroidAppReviewManager
import com.bunbeauty.shared.presentation.MainViewModel
import com.bunbeauty.shared.ui.main.IMessageHost
import com.bunbeauty.shared.ui.screen.main.MainScreen
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity :
    AppCompatActivity(R.layout.layout_compose),
    IMessageHost {
    val viewModel: MainViewModel by viewModel()
    private val notificationNavigation: NotificationNavigation by inject()
    private val notificationPermissionRequester: NotificationPermissionRequester by inject()
    private val androidAppReviewManager: AndroidAppReviewManager by inject()
    private val userRepository: UserRepo by inject()

    private val requestPermissionLauncher by lazy {
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            syncNotificationPermission()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        androidAppReviewManager.register(this)

        enableEdgeToEdge(
            navigationBarStyle =
                if (isDarkTheme()) {
                    SystemBarStyle.dark(
                        scrim = android.graphics.Color.TRANSPARENT,
                    )
                } else {
                    SystemBarStyle.light(
                        scrim = android.graphics.Color.TRANSPARENT,
                        darkScrim = android.graphics.Color.TRANSPARENT,
                    )
                },
        )

        setContent {
            val color = FoodDeliveryTheme.colors.mainColors.surface
            val statusBarColor = remember { mutableStateOf(color) }
            FoodDeliveryTheme(
                flavor = BuildConfig.FLAVOR,
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    MainScreen(
                        modifier =
                            Modifier
                                .imePadding(),
                        barColorCallback = {
                            statusBarColor.value = it
                        },
                    )

                    NavigationGradient(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        statusBarColor = statusBarColor,
                    )
                }
            }
        }

        notificationPermissionRequester.register(::checkNotificationPermission)
        checkNotificationPermission()
        handleNotificationIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        syncNotificationPermission()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent)
    }

    override fun onDestroy() {
        notificationPermissionRequester.unregister()
        androidAppReviewManager.unregister(this)
        super.onDestroy()
    }

    @Composable
    fun NavigationGradient(
        modifier: Modifier,
        statusBarColor: MutableState<Color>,
    ) {
        Spacer(
            modifier =
                modifier
                    .fillMaxWidth()
                    .height(
                        with(LocalDensity.current) {
                            WindowInsets.navigationBars.getBottom(this).toDp()
                        },
                    ).background(
                        Brush.verticalGradient(
                            colors =
                                listOf(
                                    statusBarColor.value.copy(alpha = 0.1f),
                                    statusBarColor.value.copy(alpha = 0.2f),
                                    statusBarColor.value.copy(alpha = 0.3f),
                                    statusBarColor.value.copy(alpha = 0.4f),
                                    statusBarColor.value.copy(alpha = 0.6f),
                                    statusBarColor.value.copy(alpha = 0.8f),
                                ),
                        ),
                    ).blur(radius = 10.dp),
        )
    }

    override fun showInfoMessage(
        text: String,
        paddingBottom: Int,
    ) {
        viewModel.showInfoMessage(text = text, paddingBottom = paddingBottom)
    }

    override fun showErrorMessage(text: String) {
        viewModel.showErrorMessage(text)
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun syncNotificationPermission() {
        userRepository.updateNotificationPermission(
            NotificationManagerCompat.from(this).areNotificationsEnabled(),
        )
    }

    private fun handleNotificationIntent(intent: Intent) {
        if (intent.getStringExtra("destination") != "order_details") {
            return
        }
        val notificationId = intent.getStringExtra("notificationId") ?: return
        val orderUuid = intent.getStringExtra("orderUuid") ?: return
        notificationNavigation.open(
            NotificationOpen(
                notificationId = notificationId,
                orderUuid = orderUuid,
                campaign = intent.getStringExtra("campaign").orEmpty(),
                touch = intent.getStringExtra("touch").orEmpty(),
                variant = intent.getStringExtra("variant").orEmpty(),
            ),
        )
        intent.removeExtra("destination")
        intent.removeExtra("notificationId")
        intent.removeExtra("orderUuid")
    }

    fun isDarkTheme(): Boolean =
        this.resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}
