package com.ssafy.glim.core.data.authmanager

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    fun deleteAll() {
        cachedAccessToken = null
        cachedRefreshToken = null
        cachedUserId = null

        CoroutineScope(Dispatchers.IO).launch {
            authDataStore.deleteAccessToken()
            authDataStore.deleteRefreshToken()
            authDataStore.deleteUserId()
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
            deleteAll()
        }
        return canLogin
    }
}
