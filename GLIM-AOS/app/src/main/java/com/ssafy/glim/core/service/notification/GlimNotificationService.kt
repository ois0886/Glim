package com.ssafy.glim.core.service.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ssafy.glim.R
import com.ssafy.glim.feature.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GlimNotificationService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "GlimNotificationService"
        private const val CHANNEL_ID = "glim_notifications"
    }

    @Inject
    lateinit var fcmTokenManager: FcmTokenManager

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "From: ${remoteMessage.from}")

        Log.d(TAG, "Message data payload: ${remoteMessage.data}")
        handleNotification(remoteMessage.data, remoteMessage.notification)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "new token : $token")

        fcmTokenManager.handleNewToken(token)
    }

    private fun handleNotification(
        data: Map<String, String>,
        notification: RemoteMessage.Notification?
    ) {
        Log.d(TAG, "Data: $data")
        Log.d(TAG, "Notification: $notification")

        // Firebase 자동 알림이 있는 경우 무시하고 우리 커스텀 알림만 표시
        val screen = data["screen"] ?: run {
            Log.w(TAG, "No screen type in notification data")
            return
        }

        // 커스텀 제목/내용 사용 (data에서 가져오거나 기본값)
        val title = data["title"] ?: "누군가 내 글림을 좋아합니다"
        val body = data["body"] ?: "새로운 좋아요를 받았어요!"

        when (screen) {
            "LIKE" -> {
                val quoteId = data["quoteId"]?.toLongOrNull()
                if (quoteId != null && quoteId > 0) {
                    showLikeNotification(quoteId, title, body)
                }
            }

            else -> {
                Log.w(TAG, "Unknown screen type: $screen")
            }
        }
    }

    private fun showLikeNotification(
        quoteId: Long,
        title: String,
        body: String
    ) {
        try {
            val quoteIntent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("nav_route", "glim")
                putExtra("quote_id", quoteId)
            }

            val pendingQuoteIntent = PendingIntent.getActivity(
                this,
                quoteId.toInt(),
                quoteIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingQuoteIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(quoteId.toInt(), notification)

            Log.d(TAG, "Custom notification shown: $title - $body")
        } catch (e: Exception) {
            Log.e(TAG, "Error showing notification: ${e.message}", e)
        }
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
