package com.bunbeauty.papakarlo

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import com.bunbeauty.analytic.activateAppMetrica
import com.bunbeauty.core.Constants.CHANNEL_ID
import com.bunbeauty.papakarlo.di.appModule
import com.bunbeauty.shared.data.CompanyUuidProvider
import com.bunbeauty.shared.di.initKoin
import com.bunbeauty.shared.domain.feature.notification.SubscribeToNotificationUseCase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.logger.Level
import kotlin.coroutines.CoroutineContext

private const val NOTIFICATION_NEWS_CHANNEL_NAME = "NEWS"

class FoodDeliveryApplication :
    Application(),
    KoinComponent,
    CoroutineScope {
    override val coroutineContext: CoroutineContext = SupervisorJob()

    private val subscribeToNotification: SubscribeToNotificationUseCase by inject()
    private val companyUuidProvider: CompanyUuidProvider by inject()

    override fun onCreate() {
        setTheme(R.style.AppTheme)
        super.onCreate()

        initKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@FoodDeliveryApplication)
            modules(
                appModule(),
            )
        }

        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = !BuildConfig.DEBUG
        FirebaseAnalytics
            .getInstance(applicationContext)
            .setAnalyticsCollectionEnabled(!BuildConfig.DEBUG)
        activateAppMetrica(
            context = applicationContext,
            dataSendingEnabled = !BuildConfig.DEBUG,
        )
        subscribeToNotification(companyUuid = companyUuidProvider.companyUuid)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel =
            NotificationChannel(
                CHANNEL_ID,
                NOTIFICATION_NEWS_CHANNEL_NAME,
                importance,
            ).apply {
                enableLights(true)
                enableVibration(true)

                val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val attributes =
                    AudioAttributes
                        .Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build()
                setSound(soundUri, attributes)
            }
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
