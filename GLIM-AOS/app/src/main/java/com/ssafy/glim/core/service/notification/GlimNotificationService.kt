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
        val title = notification?.title ?: "책 제목 없음"
        val body = notification?.body ?: ""
        val image = notification?.imageUrl

        when (screen) {
            "LIKE" -> {
                val quoteId = data["quoteId"]?.toLongOrNull() ?: -1L
                val bookId = data["bookId"]?.toLongOrNull() ?: -1L
                showNotification(title, body, quoteId, bookId, image)
            }

            else -> {
                showNotification(title, body)
            }
        }
    }

    private fun showNotification(
        title: String,
        body: String,
        quoteId: Long = -1L,
        bookId: Long = -1L,
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

        val bookIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            if (quoteId != -1L) {
                putExtra("nav_route", "book")
                putExtra("book_id", bookId)
            }
        }

        val pendingBookIntent = PendingIntent.getActivity(
            this,
            0,
            bookIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setLargeIcon(image?.toBitmap(this))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(body)
            )
            .addAction(
                R.drawable.ic_glim, "책 정보",
                pendingBookIntent
            )
            .addAction(
                R.drawable.ic_library, "글림",
                pendingQuoteIntent
            )
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
