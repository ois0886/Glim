package com.ssafy.glim.core.service.notification


import android.util.Log
import com.ssafy.glim.core.domain.usecase.fcm.RefreshTokenUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmTokenManager @Inject constructor(
    private val refreshTokenUseCase: RefreshTokenUseCase
) {

    companion object {
        private const val TAG = "FcmTokenManager"
    }

    /**
     * 새 토큰을 받았을 때 처리 (onNewToken에서 호출)
     */
    fun handleNewToken(token: String) {
        Log.d(TAG, "Handling new FCM token: $token")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                refreshTokenUseCase(token)
                Log.d(TAG, "FCM token refreshed successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to refresh FCM token: ${e.message}", e)
            }
        }
    }
}
