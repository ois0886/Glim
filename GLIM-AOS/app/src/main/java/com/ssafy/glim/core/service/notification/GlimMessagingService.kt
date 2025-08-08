package com.ssafy.glim.core.service.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GlimMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("NotificationService", "From: ${remoteMessage.from}")

        // 데이터 페이로드 처리
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("NotificationService", "Message data payload: ${remoteMessage.data}")
        }

        // 알림 페이로드 처리
        remoteMessage.notification?.let {
            Log.d("NotificationService", "Message Notification Body: ${it.title}, ${it.body}, ${it.imageUrl}")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("NotificationService", "new token : $token")
    }
}
