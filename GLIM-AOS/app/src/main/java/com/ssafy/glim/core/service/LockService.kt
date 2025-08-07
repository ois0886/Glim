package com.ssafy.glim.core.service

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
    private var isServiceReady = false

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service onCreate")

        // onCreate에서 즉시 foreground 시작
        try {
            createNotificationChannel()
            startForeground(SERVICE_ID, createNotificationBuilder())
            Log.d(TAG, "Started foreground successfully in onCreate")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start foreground in onCreate", e)
            // 기본 알림으로 재시도
            try {
                startForeground(SERVICE_ID, createFallbackNotification())
            } catch (fallbackError: Exception) {
                Log.e(TAG, "Failed to start foreground with fallback notification", fallbackError)
                stopSelf()
            }
        }
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        Log.d(TAG, "onStartCommand called, isServiceReady: $isServiceReady")

        // 이미 준비된 상태면 중복 실행 방지
        if (isServiceReady) {
            Log.d(TAG, "Service already ready, returning")
            return START_NOT_STICKY
        }

        try {
            // 리시버 등록만 여기서 처리 (foreground는 이미 onCreate에서 시작됨)
            startLockReceiver()
            isServiceReady = true
            Log.d(TAG, "Service setup completed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to setup service", e)
            // 실패하면 서비스 종료
            stopSelf()
            return START_NOT_STICKY
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        Log.d(TAG, "Service onDestroy")
        isServiceReady = false
        stopLockReceiver()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startLockReceiver() {
        if (!isReceiverRegistered) {
            try {
                val intentFilter = IntentFilter().apply {
                    addAction(Intent.ACTION_SCREEN_ON)
                    addAction(Intent.ACTION_SCREEN_OFF)
                }
                registerReceiver(screenReceiver, intentFilter)
                isReceiverRegistered = true
                Log.d(TAG, "Screen receiver registered successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to register screen receiver", e)
                throw e
            }
        }
    }

    private fun stopLockReceiver() {
        if (isReceiverRegistered) {
            try {
                unregisterReceiver(screenReceiver)
                isReceiverRegistered = false
                Log.d(TAG, "Screen receiver unregistered successfully")
            } catch (_: IllegalArgumentException) {
                Log.w(TAG, "Receiver was already unregistered")
                isReceiverRegistered = false
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error while unregistering receiver", e)
                isReceiverRegistered = false
            }
        }
    }

    private fun createNotificationChannel() {
        try {
            val notificationChannel = SimpleNotificationBuilder.createChannel(
                LOCK_CHANNEL,
                getStringWithContext(R.string.app_name),
                // HIGH에서 LOW로 변경
                NotificationManager.IMPORTANCE_LOW,
                getStringWithContext(R.string.app_name),
            )

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel as NotificationChannel)

            Log.d(TAG, "Notification channel created successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create notification channel", e)
            throw e
        }
    }

    private fun getStringWithContext(stringRes: Int): String {
        return try {
            applicationContext.getString(stringRes)
        } catch (_: Exception) {
            Log.w(TAG, "Failed to get string resource $stringRes, using fallback")
            "Glim Service"
        }
    }

    private fun createNotificationBuilder(): Notification {
        return try {
            SimpleNotificationBuilder.createBuilder(
                context = this,
                channelId = LOCK_CHANNEL,
                title = getStringWithContext(R.string.app_name),
                text = "백그라운드에서 실행 중",
                icon = R.drawable.ic_launcher_foreground,
            ) as Notification
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create notification with SimpleNotificationBuilder", e)
            createFallbackNotification()
        }
    }

    private fun createFallbackNotification(): Notification {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // 기본 채널 생성
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val fallbackChannel = NotificationChannel(
                FALLBACK_CHANNEL,
                "Fallback Channel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Fallback notification channel"
                setShowBadge(false)
                enableLights(false)
                enableVibration(false)
                setSound(null, null)
            }
            notificationManager.createNotificationChannel(fallbackChannel)

            Notification.Builder(this, FALLBACK_CHANNEL)
                .setContentTitle("Glim")
                .setContentText("백그라운드 실행 중")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build()
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
                .setContentTitle("Glim")
                .setContentText("백그라운드 실행 중")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build()
        }
    }

    private companion object {
        private const val TAG = "LockService"
        const val LOCK_CHANNEL = "LOCK_CHANNEL"
        const val FALLBACK_CHANNEL = "FALLBACK_CHANNEL"
        const val SERVICE_ID: Int = 1
    }
}
