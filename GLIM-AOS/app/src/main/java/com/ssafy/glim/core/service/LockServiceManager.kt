package com.ssafy.glim.core.service

import android.content.Context
import com.ssafy.glim.core.domain.usecase.setting.GetLockSettingsUseCase
import com.ssafy.glim.core.service.core.BaseForegroundServiceManager
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
) : BaseForegroundServiceManager<LockService>(
    context = applicationContext,
    targetClass = LockService::class.java,
) {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var isServiceStarted = false

    init {
        // UseCase를 통해 설정 변경 감지
        serviceScope.launch {
            getLockSettingsUseCase()
                .map { it.isEnabled }
                .distinctUntilChanged()
                .collect { enabled ->
                    if (enabled) {
                        startServiceIfNeeded()
                    } else {
                        stopServiceIfRunning()
                    }
                }
        }
    }

    private fun startServiceIfNeeded() {
        if (!isServiceStarted) {
            start()
            isServiceStarted = true
        }
    }

    private fun stopServiceIfRunning() {
        if (isServiceStarted) {
            stop()
            isServiceStarted = false
        }
    }

    override fun start() {
        super.start()
        isServiceStarted = true
    }

    override fun stop() {
        super.stop()
        isServiceStarted = false
    }
}
