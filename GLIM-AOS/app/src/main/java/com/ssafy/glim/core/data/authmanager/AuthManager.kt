package com.ssafy.glim.core.data.authmanager

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthManager @Inject constructor(
    private val authDataStore: AuthDataStore,
    private val applicationScope: CoroutineScope
) {
    @Volatile
    private var cachedAccessToken: String? = null

    @Volatile
    private var cachedRefreshToken: String? = null

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
        }
    }

    fun getAccessToken(): String? = cachedAccessToken

    fun getRefreshToken(): String? = cachedRefreshToken

    // TODO : refreshToken: String 추가
    fun saveToken(accessToken: String) {
        Log.d("AuthManager", "save token")
        // 메모리는 즉시 업데이트
        cachedAccessToken = accessToken

        CoroutineScope(Dispatchers.IO).launch {
            authDataStore.saveAccessToken(accessToken)
        }
    }
}
