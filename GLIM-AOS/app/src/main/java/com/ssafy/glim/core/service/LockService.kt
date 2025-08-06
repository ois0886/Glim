package com.ssafy.glim.core.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
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

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        createNotificationChannel()
        startForeground(SERVICE_ID, createNotificationBuilder())
        startLockReceiver()

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        stopLockReceiver()
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun startLockReceiver() {
        if (!isReceiverRegistered) {
            val intentFilter = IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_ON)
                addAction(Intent.ACTION_SCREEN_OFF)
            }
            registerReceiver(screenReceiver, intentFilter)
            isReceiverRegistered = true
        }
    }

    private fun stopLockReceiver() {
        if (isReceiverRegistered) {
            try {
                unregisterReceiver(screenReceiver)
                isReceiverRegistered = false
            } catch (_: IllegalArgumentException) {
            }
        }
    }

    private fun createNotificationChannel() {
        val notificationChannel =
            SimpleNotificationBuilder.createChannel(
                LOCK_CHANNEL,
                getStringWithContext(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH,
                getStringWithContext(R.string.app_name),
            )

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(notificationChannel as NotificationChannel)
    }

    private fun getStringWithContext(stringRes: Int): String {
        return applicationContext.getString(stringRes)
    }

    private fun createNotificationBuilder(): Notification {
        return SimpleNotificationBuilder.createBuilder(
            context = this,
            channelId = LOCK_CHANNEL,
            title = getStringWithContext(R.string.app_name),
            text = getStringWithContext(R.string.app_name),
            icon = R.drawable.ic_launcher_foreground,
        ) as Notification
    }

    private companion object {
        const val LOCK_CHANNEL = "LOCK_CHANNEL"
        const val SERVICE_ID: Int = 1
    }
}
