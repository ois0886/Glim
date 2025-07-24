package com.ssafy.glim.core.service.core

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.ssafy.glim.core.util.isServiceRunning

abstract class BaseForegroundServiceManager<T : Service>(
    val context: Context,
    val targetClass: Class<T>,
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun start() =
        synchronized(this) {
            val intent = Intent(context, targetClass)

            if (!context.isServiceRunning(targetClass)) {
                context.startForegroundService(intent)
            }
        }

    fun stop() =
        synchronized(this) {
            val intent = Intent(context, targetClass)

            if (context.isServiceRunning(targetClass)) {
                context.stopService(intent)
            }
        }
}
