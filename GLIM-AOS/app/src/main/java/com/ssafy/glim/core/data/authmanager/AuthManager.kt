package com.ssafy.glim.core.data.authmanager

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthManager @Inject constructor(
    private val authDataStore: AuthDataStore,
    applicationScope: CoroutineScope
) {
    @Volatile
    private var cachedAccessToken: String? = null

    @Volatile
    private var cachedRefreshToken: String? = null

    @Volatile
    private var cachedUserId: String? = null

    // 로그아웃 이벤트 플로우
    private val _logoutEvent = MutableSharedFlow<LogoutReason>(extraBufferCapacity = 1)
    val logoutEvent: SharedFlow<LogoutReason> = _logoutEvent

    init {
        applicationScope.launch {
            launch {
                authDataStore.accessTokenFlow.collect { token ->
                    Log.d("Init AuthManager access token", "$token")
                    cachedAccessToken = token
                }
            }

            launch {
                authDataStore.refreshTokenFlow.collect { token ->
                    Log.d("Init AuthManager refresh token", "$token")
                    cachedRefreshToken = token
                }
            }

            launch {
                authDataStore.userIdFlow.collect { id ->
                    Log.d("Init AuthManager user id", "$id")
                    cachedUserId = id
                }
            }
        }
    }

    fun getAccessToken(): String? = cachedAccessToken

    fun getRefreshToken(): String? = cachedRefreshToken

    fun getUserId(): String? = cachedUserId

    fun saveToken(accessToken: String, refreshToken: String) {
        Log.d("AuthManager", "save token")
        cachedAccessToken = accessToken
        cachedRefreshToken = refreshToken

        CoroutineScope(Dispatchers.IO).launch {
            authDataStore.saveAccessToken(accessToken)
            authDataStore.saveRefreshToken(refreshToken)
        }
    }

    fun saveUserInfo(userId: String) {
        Log.d("AuthManager", "save userInfo")
        cachedUserId = userId

        CoroutineScope(Dispatchers.IO).launch {
            authDataStore.saveUserId(userId)
        }
    }

    fun logout(reason: LogoutReason, eventEmit: Boolean = true) {
        Log.d("AuthManager", "logout - reason: $reason")

        // 메모리 캐시 즉시 삭제
        cachedAccessToken = null
        cachedRefreshToken = null
        cachedUserId = null

        CoroutineScope(Dispatchers.IO).launch {
            // DataStore에서 삭제
            authDataStore.deleteAccessToken()
            authDataStore.deleteRefreshToken()
            authDataStore.deleteUserId()

            // 로그아웃 이벤트 발생
            if (eventEmit) _logoutEvent.emit(reason)
        }
    }

    fun canAutoLogin(): Boolean {
        val hasAccessToken = !cachedAccessToken.isNullOrBlank()
        val hasRefreshToken = !cachedRefreshToken.isNullOrBlank()
        val hasUserId = !cachedUserId.isNullOrBlank()

        Log.d("AuthManager", "canAutoLogin - access: $hasAccessToken, refresh: $hasRefreshToken, userId: $hasUserId")

        val canLogin = hasAccessToken && hasRefreshToken && hasUserId
        if (!canLogin) {
            Log.d("AuthManager", "Missing auth data, clearing all")
            logout(reason = LogoutReason.UnknownError, eventEmit = false)
        }
        return canLogin
    }
}
