package com.ssafy.glim.core.service.core

import android.app.Service
import android.content.Context
import android.content.Intent
import android.util.Log
import com.ssafy.glim.core.util.isServiceRunning
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

abstract class BaseForegroundServiceManager(
    val context: Context,
    val targetClass: Class<out Service>,
) {
    // 동시 실행 방지를 위한 뮤텍스
    private val operationMutex = Mutex()

    open suspend fun start() = operationMutex.withLock {
        try {
            Log.d(TAG, "Starting service: ${targetClass.simpleName}")

            if (!context.isServiceRunning(targetClass)) {
                val intent = Intent(context, targetClass)
                context.startForegroundService(intent)
                Log.d(TAG, "Service start command sent: ${targetClass.simpleName}")

                // 서비스가 시작될 때까지 잠시 대기
                delay(300)

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

    open suspend fun stop() = operationMutex.withLock {
        try {
            Log.d(TAG, "Stopping service: ${targetClass.simpleName}")

            if (context.isServiceRunning(targetClass)) {
                val intent = Intent(context, targetClass)
                val result = context.stopService(intent)
                Log.d(TAG, "Service stop command sent: ${targetClass.simpleName}, result: $result")

                // 서비스가 중지될 때까지 잠시 대기
                delay(300)

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

    // 비동기 버전 (기존 코드와의 호환성을 위해)
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
    }
}
