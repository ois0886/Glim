package com.ssafy.glim.core.data.authmanager

import android.util.Log
import com.ssafy.glim.core.data.dto.token.AuthToken
import com.ssafy.glim.core.data.service.AuthService
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val authManager: AuthManager,
    private val authService: AuthService
) : Authenticator {

    private val retryCount = AtomicInteger(0)
    private val maxRetryCount = 3

    override fun authenticate(route: Route?, response: Response): Request? {
        return when (val result = handleAuthentication(response)) {
            is AuthResult.Success -> result.request
            is AuthResult.Failure -> {
                Log.w("TokenAuthenticator", "인증 실패: ${result.reason}")
                null
            }
        }
    }

    private fun handleAuthentication(response: Response): AuthResult {
        return try {
            if (retryCount.get() >= maxRetryCount) {
                Log.w("TokenAuthenticator", "최대 재시도 횟수 초과")
                authManager.logout(LogoutReason.MaxRetryExceeded)
                return AuthResult.Failure("최대 재시도 횟수 초과")
            }

            retryCount.incrementAndGet()
            Log.d("TokenAuthenticator", "토큰 갱신 시도 ${retryCount.get()}/$maxRetryCount")

            when (val tokenResult = getCurrentTokens()) {
                is TokenResult.Success -> {
                    if (isTokenAlreadyRefreshed(response, tokenResult.tokens.accessToken)) {
                        Log.d("TokenAuthenticator", "토큰이 이미 갱신됨")
                        resetRetryCount()
                        AuthResult.Success(createRequestWithToken(response.request, tokenResult.tokens.accessToken))
                    } else {
                        refreshTokenAndCreateRequest(response.request, tokenResult.tokens)
                    }
                }

                is TokenResult.Failure -> {
                    authManager.logout(LogoutReason.TokenNotFound)
                    AuthResult.Failure(tokenResult.reason)
                }
            }
        } catch (e: Exception) {
            val errorMsg = "토큰 인증 처리 중 오류: ${e.message}"
            Log.e("TokenAuthenticator", errorMsg, e)
            authManager.logout(LogoutReason.UnknownError)
            AuthResult.Failure(errorMsg)
        }
    }

    private fun getCurrentTokens(): TokenResult {
        val accessToken = authManager.getAccessToken()
        val refreshToken = authManager.getRefreshToken()

        return when {
            accessToken.isNullOrBlank() -> TokenResult.Failure("Access token 없음")
            refreshToken.isNullOrBlank() -> TokenResult.Failure("Refresh token 없음")
            else -> TokenResult.Success(TokenPair(accessToken, refreshToken))
        }
    }

    private fun isTokenAlreadyRefreshed(response: Response, currentAccessToken: String): Boolean {
        val requestToken = response.request.header("Authorization")
            ?.removePrefix("Bearer ")
            ?.trim()
        return currentAccessToken != requestToken
    }

    private fun refreshTokenAndCreateRequest(originalRequest: Request, tokens: TokenPair): AuthResult {
        return try {
            Log.d("TokenAuthenticator", "토큰 갱신 시작")

            when (val refreshResult = runBlocking { performTokenRefresh(tokens.refreshToken) }) {
                is RefreshResult.Success -> {
                    authManager.saveToken(refreshResult.tokenData.accessToken, refreshResult.tokenData.refreshToken)
                    resetRetryCount()
                    Log.d("TokenAuthenticator", "토큰 갱신 성공")
                    AuthResult.Success(createRequestWithToken(originalRequest, refreshResult.tokenData.accessToken))
                }

                is RefreshResult.Failure -> {
                    handleAuthFailure(refreshResult.reason)
                    AuthResult.Failure(refreshResult.reason)
                }

                is RefreshResult.Logout -> {
                    authManager.logout(LogoutReason.RefreshTokenExpired)
                    AuthResult.Failure(refreshResult.reason)
                }
            }
        } catch (e: Exception) {
            val errorMsg = "토큰 갱신 중 오류: ${e.message}"
            Log.e("TokenAuthenticator", errorMsg, e)
            authManager.logout(LogoutReason.UnknownError)
            AuthResult.Failure(errorMsg)
        }
    }

    private suspend fun performTokenRefresh(refreshToken: String): RefreshResult {
        return try {
            val authHeader = "Bearer $refreshToken"
            val response = authService.refreshToken(authHeader)

            when {
                response.isSuccessful -> {
                    response.body()?.let { tokenData ->
                        RefreshResult.Success(tokenData)
                    } ?: RefreshResult.Logout("응답 body가 null")
                }

                response.code() in 401..403 -> {
                    RefreshResult.Logout("Refresh token 만료")
                }

                else -> {
                    RefreshResult.Failure("HTTP 오류: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            RefreshResult.Failure("네트워크 오류: ${e.message}")
        }
    }

    private fun createRequestWithToken(originalRequest: Request, token: String): Request {
        return originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
    }

    private fun resetRetryCount() {
        retryCount.set(0)
    }

    private fun handleAuthFailure(reason: String) {
        Log.w("TokenAuthenticator", "인증 실패 처리: $reason")
        resetRetryCount()
    }

    // Sealed Classes
    private sealed class AuthResult {
        data class Success(val request: Request) : AuthResult()
        data class Failure(val reason: String) : AuthResult()
    }

    private sealed class TokenResult {
        data class Success(val tokens: TokenPair) : TokenResult()
        data class Failure(val reason: String) : TokenResult()
    }

    private sealed class RefreshResult {
        data class Success(val tokenData: AuthToken) : RefreshResult()
        data class Failure(val reason: String) : RefreshResult()
        data class Logout(val reason: String) : RefreshResult()
    }

    private data class TokenPair(
        val accessToken: String,
        val refreshToken: String
    )
}
