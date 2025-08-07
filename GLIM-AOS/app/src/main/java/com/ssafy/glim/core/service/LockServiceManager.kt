package com.ssafy.glim.core.service

import android.content.Context
import android.content.Intent
import android.util.Log
import com.ssafy.glim.core.domain.usecase.setting.GetLockSettingsUseCase
import com.ssafy.glim.core.service.core.BaseForegroundServiceManager
import com.ssafy.glim.core.util.isServiceRunning
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LockServiceManager @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val getLockSettingsUseCase: GetLockSettingsUseCase
) : BaseForegroundServiceManager(
    context = applicationContext,
    targetClass = LockService::class.java,
) {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    @Volatile
    private var lastKnownState: Boolean? = null

    @Volatile
    private var isInitialized = false

    init {
        Log.d(TAG, "LockServiceManager initialized")
        observeSettingsChanges()
    }

    private fun observeSettingsChanges() {
        serviceScope.launch {
            try {
                getLockSettingsUseCase()
                    .map { it.isEnabled }
                    .distinctUntilChanged()
                    .collect { enabled ->
                        Log.d(TAG, "Lock setting changed: $enabled")
                        handleServiceStateChange(enabled)
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error in settings flow", e)
                delay(RETRY_DELAY)
                observeSettingsChanges()
            }
        }
    }

    private suspend fun handleServiceStateChange(enabled: Boolean) {
        val previousState = lastKnownState
        lastKnownState = enabled

        if (!isInitialized) {
            isInitialized = true
            Log.d(TAG, "Initial settings loaded: enabled=$enabled")
        } else if (enabled == previousState) {
            Log.d(TAG, "State unchanged, ignoring: $enabled")
            return
        }

        val isCurrentlyRunning = applicationContext.isServiceRunning(LockService::class.java)
        Log.d(TAG, "Handling state change: enabled=$enabled, previousState=$previousState, currentlyRunning=$isCurrentlyRunning")

        if (enabled) {
            if (!isCurrentlyRunning) {
                startServiceWithRetry()
            }
        } else {
            if (isCurrentlyRunning) {
                stopServiceWithRetry()
            }
        }
    }

    override suspend fun start() {
        if (!isInitialized) {
            Log.d(TAG, "Settings not loaded yet, deferring start")
            return
        }

        if (lastKnownState == true) {
            startServiceWithRetry()
        } else {
            Log.d(TAG, "Service disabled in settings, ignoring start request")
        }
    }

    override suspend fun stop() {
        if (!isInitialized) {
            Log.d(TAG, "Settings not loaded yet, deferring stop")
            return
        }

        stopServiceWithRetry()
    }

    private suspend fun startServiceWithRetry() {
        if (applicationContext.isServiceRunning(LockService::class.java)) {
            Log.d(TAG, "Service is already running, ignoring start request")
            return
        }

        repeat(MAX_RETRY_COUNT) { attempt ->
            try {
                Log.d(TAG, "Starting service... attempt ${attempt + 1}")

                super.start()

                delay(START_TIMEOUT)

                if (applicationContext.isServiceRunning(LockService::class.java)) {
                    Log.d(TAG, "Service started successfully")
                    return
                } else {
                    throw Exception("Service failed to start")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Start attempt ${attempt + 1} failed", e)
                if (attempt == MAX_RETRY_COUNT - 1) {
                    Log.e(TAG, "Failed to start service after $MAX_RETRY_COUNT attempts")
                    throw e
                }
                delay(RETRY_DELAY * (attempt + 1))
            }
        }
    }

    private suspend fun stopServiceWithRetry() {
        if (!applicationContext.isServiceRunning(LockService::class.java)) {
            Log.d(TAG, "Service is not running, ignoring stop request")
            return
        }

        repeat(MAX_RETRY_COUNT) { attempt ->
            try {
                Log.d(TAG, "Stopping service... attempt ${attempt + 1}")

                super.stop()

                delay(STOP_TIMEOUT)

                if (!applicationContext.isServiceRunning(LockService::class.java)) {
                    Log.d(TAG, "Service stopped successfully")
                    return
                } else {
                    throw Exception("Service failed to stop")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Stop attempt ${attempt + 1} failed", e)
                if (attempt == MAX_RETRY_COUNT - 1) {
                    Log.e(TAG, "Failed to stop service after $MAX_RETRY_COUNT attempts")
                    throw e
                }
                delay(RETRY_DELAY * (attempt + 1))
            }
        }
    }

    override fun startSync() {
        try {
            if (!applicationContext.isServiceRunning(LockService::class.java)) {
                val intent = Intent(applicationContext, LockService::class.java)
                applicationContext.startForegroundService(intent)
                Log.d(TAG, "Service started synchronously")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start service synchronously", e)
            throw e
        }
    }

    override fun stopSync() {
        try {
            if (applicationContext.isServiceRunning(LockService::class.java)) {
                val intent = Intent(applicationContext, LockService::class.java)
                applicationContext.stopService(intent)
                Log.d(TAG, "Service stopped synchronously")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop service synchronously", e)
            throw e
        }
    }

    private companion object {
        private const val TAG = "LockServiceManager"
        private const val MAX_RETRY_COUNT = 3
        private const val RETRY_DELAY = 1000L
        private const val START_TIMEOUT = 500L
        private const val STOP_TIMEOUT = 300L
    }
}
