package com.ssafy.glim.core.service.core

import android.app.Service
import android.content.Context
import android.content.Intent
import com.ssafy.glim.core.util.isServiceRunning

abstract class BaseForegroundServiceManager(
    val context: Context,
    val targetClass: Class<out Service>,
) {
    open fun start() = synchronized(this) {
        if (!context.isServiceRunning(targetClass)) {
            val intent = Intent(context, targetClass)
            try {
                context.startForegroundService(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    open fun stop() = synchronized(this) {
        if (context.isServiceRunning(targetClass)) {
            val intent = Intent(context, targetClass)
            try {
                context.stopService(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
