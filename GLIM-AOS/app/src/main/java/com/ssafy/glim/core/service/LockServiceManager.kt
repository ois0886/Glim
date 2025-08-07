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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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

    // 서비스 상태 관리
    private var isServiceStarting = false
    private var isServiceStopping = false
    private var lastKnownState = false
    private val stateMutex = Mutex()

    init {
        Log.d(TAG, "LockServiceManager initialized")
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
            }
        }
    }

    private suspend fun handleServiceStateChange(enabled: Boolean) = stateMutex.withLock {
        // 상태가 실제로 변경되었을 때만 처리
        if (enabled == lastKnownState) {
            Log.d(TAG, "State unchanged, ignoring: $enabled")
            return@withLock
        }

        val isCurrentlyRunning = applicationContext.isServiceRunning(LockService::class.java)
        Log.d(TAG, "Handling state change: enabled=$enabled, lastKnown=$lastKnownState, currentlyRunning=$isCurrentlyRunning")

        lastKnownState = enabled

        if (enabled) {
            startServiceSafely()
        } else {
            stopServiceSafely()
        }
    }

    // BaseForegroundServiceManager의 suspend 메서드들 오버라이드
    override suspend fun start() {
        startServiceSafely()
    }

    override suspend fun stop() {
        stopServiceSafely()
    }

    private suspend fun startServiceSafely() {
        // 이미 시작 중이거나 실행 중이면 무시
        if (isServiceStarting) {
            Log.d(TAG, "Service is already starting, ignoring start request")
            return
        }

        if (applicationContext.isServiceRunning(LockService::class.java)) {
            Log.d(TAG, "Service is already running, ignoring start request")
            return
        }

        isServiceStarting = true
        try {
            Log.d(TAG, "Starting service...")

            // 약간의 지연을 두어 중복 호출 방지
            delay(100)

            // 다시 한 번 상태 확인 (지연 시간 동안 변경될 수 있음)
            if (lastKnownState && !applicationContext.isServiceRunning(LockService::class.java)) {
                super.start() // 부모 클래스의 start 메서드 호출
                Log.d(TAG, "Service start request sent")

                // 서비스가 실제로 시작될 때까지 잠시 대기
                delay(500)

                val isRunningAfterStart = applicationContext.isServiceRunning(LockService::class.java)
                Log.d(TAG, "Service running after start: $isRunningAfterStart")
            } else {
                Log.d(TAG, "Service start cancelled due to state change or already running")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start service", e)
        } finally {
            isServiceStarting = false
        }
    }

    private suspend fun stopServiceSafely() {
        // 이미 중지 중이거나 실행되지 않으면 무시
        if (isServiceStopping) {
            Log.d(TAG, "Service is already stopping, ignoring stop request")
            return
        }

        if (!applicationContext.isServiceRunning(LockService::class.java)) {
            Log.d(TAG, "Service is not running, ignoring stop request")
            return
        }

        isServiceStopping = true
        try {
            Log.d(TAG, "Stopping service...")

            // 약간의 지연을 두어 중복 호출 방지
            delay(100)

            // 다시 한 번 상태 확인 (지연 시간 동안 변경될 수 있음)
            if (!lastKnownState && applicationContext.isServiceRunning(LockService::class.java)) {
                super.stop() // 부모 클래스의 stop 메서드 호출
                Log.d(TAG, "Service stop request sent")

                // 서비스가 실제로 중지될 때까지 잠시 대기
                delay(500)

                val isRunningAfterStop = applicationContext.isServiceRunning(LockService::class.java)
                Log.d(TAG, "Service running after stop: $isRunningAfterStop")
            } else {
                Log.d(TAG, "Service stop cancelled due to state change or not running")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop service", e)
        } finally {
            isServiceStopping = false
        }
    }

    // 동기 메서드들 (BaseForegroundServiceManager 오버라이드)
    override fun startSync() {
        try {
            if (!applicationContext.isServiceRunning(LockService::class.java)) {
                val intent = Intent(applicationContext, LockService::class.java)
                applicationContext.startForegroundService(intent)
                Log.d(TAG, "Service started synchronously via override")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start service synchronously via override", e)
        }
    }

    override fun stopSync() {
        try {
            if (applicationContext.isServiceRunning(LockService::class.java)) {
                val intent = Intent(applicationContext, LockService::class.java)
                applicationContext.stopService(intent)
                Log.d(TAG, "Service stopped synchronously via override")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop service synchronously via override", e)
        }
    }

    // 수동으로 서비스 재시작이 필요한 경우
    fun restartService() {
        Log.d(TAG, "Service restart requested")
        serviceScope.launch {
            stateMutex.withLock {
                try {
                    if (applicationContext.isServiceRunning(LockService::class.java)) {
                        Log.d(TAG, "Force stopping service for restart")
                        stopSync()
                        delay(1000) // 완전히 종료될 때까지 대기
                    }

                    if (lastKnownState) {
                        Log.d(TAG, "Force starting service after restart")
                        startSync()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error during service restart", e)
                }
            }
        }
    }

    // 현재 상태 확인용 메서드
    fun getCurrentState(): Boolean = lastKnownState

    fun isServiceCurrentlyRunning(): Boolean = applicationContext.isServiceRunning(LockService::class.java)

    private companion object {
        private const val TAG = "LockServiceManager"
    }
}
