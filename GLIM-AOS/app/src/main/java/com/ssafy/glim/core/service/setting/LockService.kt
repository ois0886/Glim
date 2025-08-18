package com.ssafy.glim.core.service.setting

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import com.ssafy.glim.R
import com.ssafy.glim.core.receiver.ScreenReceiver
import com.ssafy.glim.core.util.SimpleNotificationBuilder
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject

@AndroidEntryPoint
class LockService : Service() {
    @Inject
    lateinit var screenReceiver: ScreenReceiver

    private var isReceiverRegistered = false

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service onCreate")

        createNotificationChannel()
        startForeground(SERVICE_ID, createNotification())
        registerScreenReceiver()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        Log.d(TAG, "Service onDestroy")
        unregisterScreenReceiver()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun registerScreenReceiver() {
        if (!isReceiverRegistered) {
            try {
                val intentFilter = IntentFilter().apply {
                    addAction(Intent.ACTION_SCREEN_ON)
                    addAction(Intent.ACTION_SCREEN_OFF)
                }
                registerReceiver(screenReceiver, intentFilter)
                isReceiverRegistered = true
                Log.d(TAG, "Screen receiver registered")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to register screen receiver", e)
            }
        }
    }

    private fun unregisterScreenReceiver() {
        if (isReceiverRegistered) {
            try {
                unregisterReceiver(screenReceiver)
                isReceiverRegistered = false
                Log.d(TAG, "Screen receiver unregistered")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to unregister screen receiver", e)
            }
        }
    }

    private fun createNotificationChannel() {
        val notificationChannel = SimpleNotificationBuilder.createChannel(
            LOCK_CHANNEL,
            "Lock Service",
            NotificationManager.IMPORTANCE_LOW,
            "백그라운드 잠금 서비스",
        )
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel as NotificationChannel)
    }

    private fun createNotification(): Notification {
        return SimpleNotificationBuilder.createBuilder(
            context = this,
            channelId = LOCK_CHANNEL,
            title = getString(R.string.app_name),
            text = "백그라운드에서 실행 중",
            icon = R.drawable.ic_launcher_foreground,
        ) as Notification
    }

    private companion object {
        private const val TAG = "LockService"
        private const val LOCK_CHANNEL = "LOCK_CHANNEL"
        private const val SERVICE_ID = 1
    }
}
