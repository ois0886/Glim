package com.ssafy.glim.core.util

import android.app.ActivityManager
import android.content.Context

// TODO: deprecated된 부분 수정
inline fun <reified T> Context.isServiceRunning() =
    (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
        .getRunningServices(Integer.MAX_VALUE)
        .any { it.service.className == T::class.java.name }

fun <T> Context.isServiceRunning(service: Class<T>) =
    (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
        .getRunningServices(Integer.MAX_VALUE)
        .any { it.service.className == service.name }
