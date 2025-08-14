package com.ssafy.glim.core.service.notification

import android.util.Log
import com.ssafy.glim.core.domain.usecase.setting.GetSettingsFlowUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationPermissionManager @Inject constructor(
    private val getSettingsFlowUseCase: GetSettingsFlowUseCase
) {

    companion object {
        private const val TAG = "NotificationPermissionManager"
    }

    /**
     * 알림을 표시할 수 있는지 확인
     */
    suspend fun shouldShowNotification(): Boolean = runCatching {
        // Flow에서 현재 설정값 가져오기
        val settings = getSettingsFlowUseCase().first()

        // 1. 전체 알림이 비활성화된 경우
        if (!settings.allNotificationsEnabled) {
            Log.d(TAG, "All notifications are disabled")
            return@runCatching false
        }

        true
    }.onFailure { e ->
        Log.e(TAG, "Error checking notification permission: ${e.message}", e)
    }.getOrDefault(false)
}
