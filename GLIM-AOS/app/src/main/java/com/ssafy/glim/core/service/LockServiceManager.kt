package com.ssafy.glim.core.service

import android.content.Context
import com.ssafy.glim.core.domain.usecase.setting.GetLockSettingsUseCase
import com.ssafy.glim.core.service.core.BaseForegroundServiceManager
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
    private val getLockSettingsUseCase: GetLockSettingsUseCase
) : BaseForegroundServiceManager(
    context = applicationContext,
    targetClass = LockService::class.java,
) {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        serviceScope.launch {
            getLockSettingsUseCase()
                .map { it.isEnabled }
                .distinctUntilChanged()
                .collect { enabled ->
                    if (enabled) {
                        startService()
                    } else {
                        stopService()
                    }
                }
        }
    }

    private fun startService() {
        if (!applicationContext.isServiceRunning(LockService::class.java)) {
            start()
        }
    }

    private fun stopService() {
        if (applicationContext.isServiceRunning(LockService::class.java)) {
            stop()
        }
    }
}
