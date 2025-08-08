package com.ssafy.glim.core.service.setting

import android.content.Context
import android.content.Intent
import android.util.Log
import com.ssafy.glim.core.domain.usecase.setting.GetSettingsFlowUseCase
import com.ssafy.glim.core.util.isServiceRunning
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LockServiceManager @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val getSettingsFlowUseCase: GetSettingsFlowUseCase
) {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    init {
        Log.d(TAG, "LockServiceManager initialized")
        serviceScope.launch {
            getSettingsFlowUseCase()
                .map { it.isShowGlimEnabled }
                .distinctUntilChanged()
                .collect { enabled ->
                    Log.d(TAG, "Lock setting changed: $enabled")
                    handleServiceStateChange(enabled)
                }
        }
    }

    private fun handleServiceStateChange(enabled: Boolean) {
        val isCurrentlyRunning = applicationContext.isServiceRunning(LockService::class.java)

        if (enabled && !isCurrentlyRunning) {
            startService()
        } else if (!enabled && isCurrentlyRunning) {
            stopService()
        }
    }

    private fun startService() {
        try {
            val intent = Intent(applicationContext, LockService::class.java)
            applicationContext.startForegroundService(intent)
            Log.d(TAG, "Service start command sent")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start service", e)
        }
    }

    private fun stopService() {
        try {
            val intent = Intent(applicationContext, LockService::class.java)
            applicationContext.stopService(intent)
            Log.d(TAG, "Service stop command sent")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop service", e)
        }
    }

    private companion object {
        private const val TAG = "LockServiceManager"
    }
}
