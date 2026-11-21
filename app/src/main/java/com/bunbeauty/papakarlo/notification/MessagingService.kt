package com.bunbeauty.papakarlo.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bunbeauty.core.Constants.CHANNEL_ID
import com.bunbeauty.core.Logger
import com.bunbeauty.core.Logger.NOTIFICATION_TAG
import com.bunbeauty.core.domain.repo.UserRepo
import com.bunbeauty.papakarlo.R
import com.bunbeauty.papakarlo.feature.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.android.ext.android.inject

private const val NOTIFICATION_ID = 1

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MessagingService : FirebaseMessagingService() {
    private val userRepository: UserRepo by inject()

    @SuppressLint("InlinedApi")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Logger.logD(NOTIFICATION_TAG, "onMessageReceived")

        val isNotificationPermissionGranted =
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED

        if (isNotificationPermissionGranted) {
            Logger.logD(NOTIFICATION_TAG, "isNotificationPermissionGranted")
            remoteMessage.notification?.let { notification ->
                Logger.logD(NOTIFICATION_TAG, "showNotification")
                showNotification(remoteMessage)
            }
        }
    }

    override fun onNewToken(token: String) {
        Log.d(NOTIFICATION_TAG, "onNewToken $token")
        userRepository.updateNotificationToken(notificationToken = token)
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(remoteMessage: RemoteMessage) {
        val notification = remoteMessage.notification ?: return
        val notificationId = remoteMessage.data["notificationId"]
        val notificationRequestCode = notificationId?.hashCode() ?: NOTIFICATION_ID
        val intent =
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                remoteMessage.data.forEach { (key, value) -> putExtra(key, value) }
            }
        val pendingIntent = PendingIntent.getActivity(
            this,
            notificationRequestCode,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE,
        )

        val builder =
            NotificationCompat
                .Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_medium)
                .setContentTitle(notification.title)
                .setContentText(notification.body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(notification.body))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)

        NotificationManagerCompat.from(this).notify(notificationRequestCode, builder.build())
    }
}
