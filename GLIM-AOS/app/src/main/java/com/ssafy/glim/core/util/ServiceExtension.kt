package com.ssafy.glim.core.util

import android.app.ActivityManager
import android.content.Context
import android.util.Log

@Suppress("DEPRECATION")
inline fun <reified T> Context.isServiceRunning(): Boolean {
    return try {
        (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
            .getRunningServices(Integer.MAX_VALUE)
            .any { it.service.className == T::class.java.name && it.service.packageName == packageName }
    } catch (e: SecurityException) {
        Log.d("inline Context.isServiceRunning()", "SecurityException + " + e.message)
        false
    }
}

@Suppress("DEPRECATION")
fun <T> Context.isServiceRunning(service: Class<T>): Boolean {
    return try {
        (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
            .getRunningServices(Integer.MAX_VALUE)
            .any { it.service.className == service.name && it.service.packageName == packageName }
    } catch (e: SecurityException) {
        Log.d("Context.isServiceRunning()", "SecurityException + " + e.message)
        false
    }
}
