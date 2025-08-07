package com.ssafy.glim.core.service.core

import android.app.Service
import android.content.Context
import android.content.Intent
import android.util.Log
import com.ssafy.glim.core.util.isServiceRunning
import kotlinx.coroutines.delay

abstract class BaseForegroundServiceManager(
    val context: Context,
    val targetClass: Class<out Service>,
) {
    open suspend fun start() {
        try {
            Log.d(TAG, "Starting service: ${targetClass.simpleName}")

            if (!context.isServiceRunning(targetClass)) {
                val intent = Intent(context, targetClass)
                context.startForegroundService(intent)
                Log.d(TAG, "Service start command sent: ${targetClass.simpleName}")

                delay(START_CHECK_DELAY)

                val isRunning = context.isServiceRunning(targetClass)
                Log.d(TAG, "Service running after start: $isRunning")

                if (!isRunning) {
                    Log.w(TAG, "Service may not have started properly: ${targetClass.simpleName}")
                }
            } else {
                Log.d(TAG, "Service already running: ${targetClass.simpleName}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start service: ${targetClass.simpleName}", e)
            throw e
        }
    }

    open suspend fun stop() {
        try {
            Log.d(TAG, "Stopping service: ${targetClass.simpleName}")

            if (context.isServiceRunning(targetClass)) {
                val intent = Intent(context, targetClass)
                val result = context.stopService(intent)
                Log.d(TAG, "Service stop command sent: ${targetClass.simpleName}, result: $result")

                delay(STOP_CHECK_DELAY)

                val isRunning = context.isServiceRunning(targetClass)
                Log.d(TAG, "Service running after stop: $isRunning")

                if (isRunning) {
                    Log.w(TAG, "Service may not have stopped properly: ${targetClass.simpleName}")
                }
            } else {
                Log.d(TAG, "Service not running: ${targetClass.simpleName}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop service: ${targetClass.simpleName}", e)
            throw e
        }
    }

    open fun startSync() {
        try {
            Log.d(TAG, "Starting service synchronously: ${targetClass.simpleName}")

            if (!context.isServiceRunning(targetClass)) {
                val intent = Intent(context, targetClass)
                context.startForegroundService(intent)
                Log.d(TAG, "Service start command sent synchronously: ${targetClass.simpleName}")
            } else {
                Log.d(TAG, "Service already running (sync): ${targetClass.simpleName}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start service synchronously: ${targetClass.simpleName}", e)
            throw e
        }
    }

    open fun stopSync() {
        try {
            Log.d(TAG, "Stopping service synchronously: ${targetClass.simpleName}")

            if (context.isServiceRunning(targetClass)) {
                val intent = Intent(context, targetClass)
                val result = context.stopService(intent)
                Log.d(TAG, "Service stop command sent synchronously: ${targetClass.simpleName}, result: $result")
            } else {
                Log.d(TAG, "Service not running (sync): ${targetClass.simpleName}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop service synchronously: ${targetClass.simpleName}", e)
            throw e
        }
    }

    private companion object {
        private const val TAG = "BaseForegroundServiceManager"
        private const val START_CHECK_DELAY = 300L
        private const val STOP_CHECK_DELAY = 300L
    }
}
