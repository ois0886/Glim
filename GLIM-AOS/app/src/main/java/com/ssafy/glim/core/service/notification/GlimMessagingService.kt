package com.ssafy.glim.core.service.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ssafy.glim.R
import com.ssafy.glim.feature.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GlimMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "GlimMessagingService"
        private const val CHANNEL_ID = "glim_notifications"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "From: ${remoteMessage.from}")

        // 데이터 페이로드 처리
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            handleNotification(remoteMessage.data, remoteMessage.notification)
        }

        // 알림 페이로드만 있는 경우
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification: ${it.title}, ${it.body}")
            if (remoteMessage.data.isEmpty()) {
                showNotification(it.title ?: "글림", it.body ?: "")
            }
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

        when (screen) {
            "LIKE" -> {
                val quoteId = data["quoteId"]?.toLongOrNull() ?: -1L
                showNotification(title, body, quoteId)
            }

            else -> {
                showNotification(title, body)
            }
        }
    }

    private fun showNotification(
        title: String,
        body: String,
        quoteId: Long = -1L
    ) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            if (quoteId != -1L) {
                putExtra("nav_route", "glim")
                putExtra("quote_id", quoteId)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "글림 알림",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
