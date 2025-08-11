package com.ssafy.glim.core.service.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ssafy.glim.R
import com.ssafy.glim.core.common.extensions.toBitmap
import com.ssafy.glim.feature.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GlimNotificationService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "GlimNotificationService"
        private const val CHANNEL_ID = "glim_notifications"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "From: ${remoteMessage.from}")

        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            handleNotification(remoteMessage.data, remoteMessage.notification)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "new token : $token")
    }

    private fun handleNotification(data: Map<String, String>, notification: RemoteMessage.Notification?) {
        val screen = data["screen"]
        val image = notification?.imageUrl

        when (screen) {
            "LIKE" -> {
                val quoteId = data["quoteId"]?.toLongOrNull() ?: -1L
                showLikeNotification(quoteId, image)
            }

            else -> {
                Unit
            }
        }
    }

    // 좋아요 알람
    private fun showLikeNotification(
        quoteId: Long = -1L,
        image: Uri? = null
    ) {
        val quoteIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            if (quoteId != -1L) {
                putExtra("nav_route", "glim")
                putExtra("quote_id", quoteId)
            }
        }

        val pendingQuoteIntent = PendingIntent.getActivity(
            this,
            0,
            quoteIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("누군가 내 글림을 좋아합니다")
            .setContentIntent(pendingQuoteIntent)
            .setLargeIcon(image?.toBitmap(this))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "글림 알림",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
