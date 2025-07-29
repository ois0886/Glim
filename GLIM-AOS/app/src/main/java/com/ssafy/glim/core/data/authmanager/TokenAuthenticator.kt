package com.ssafy.glim.core.data.authmanager

import android.util.Log
import com.ssafy.glim.core.data.service.AuthService
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val authManager: AuthManager,
    private val authService: AuthService
) : Authenticator {

    // Thread-safe한 카운터 사용
    private val retryCount = AtomicInteger(0)
    private val maxRetryCount = 3

    // 토큰 갱신 중인지 확인하는 플래그
    @Volatile
    private var isRefreshing = false

    override fun authenticate(route: Route?, response: Response): Request? {
        return try {
            // 최대 재시도 횟수 체크
            if (retryCount.get() >= maxRetryCount) {
                Log.w("TokenAuthenticator", "최대 재시도 횟수($maxRetryCount) 초과")
                handleAuthFailure("최대 재시도 횟수 초과")
                return null
            }

            retryCount.incrementAndGet()
            Log.d("TokenAuthenticator", "토큰 갱신 시도 ${retryCount.get()}/$maxRetryCount")

            val currentTokens = getCurrentTokens()
                ?: return handleAuthFailure("토큰 정보 없음")

            synchronized(this) {
                // 이미 토큰이 갱신되었는지 확인
                if (isTokenAlreadyRefreshed(response, currentTokens.accessToken)) {
                    Log.d("TokenAuthenticator", "토큰이 이미 갱신됨, 새 토큰으로 재시도")
                    resetRetryCount()
                    return createRequestWithToken(response.request, currentTokens.accessToken)
                }

                // 다른 스레드에서 이미 토큰 갱신 중인 경우 대기
                if (isRefreshing) {
                    Log.d("TokenAuthenticator", "다른 스레드에서 토큰 갱신 중, 대기")
                    return waitForRefreshAndRetry(response.request)
                }

                return refreshTokenAndCreateRequest(response.request, currentTokens)
            }
        } catch (e: Exception) {
            Log.e("TokenAuthenticator", "토큰 인증 처리 중 오류", e)
            handleAuthFailure("토큰 인증 오류: ${e.message}")
            null
        }
    }

    private fun getCurrentTokens(): TokenPair? {
        val accessToken = authManager.getAccessToken()
        val refreshToken = authManager.getRefreshToken()

        return if (!accessToken.isNullOrBlank() && !refreshToken.isNullOrBlank()) {
            TokenPair(accessToken, refreshToken)
        } else {
            Log.w("TokenAuthenticator", "토큰 정보가 없음 - access: ${accessToken != null}, refresh: ${refreshToken != null}")
            null
        }
    }

    private fun isTokenAlreadyRefreshed(response: Response, currentAccessToken: String): Boolean {
        val requestToken = response.request.header("Authorization")
            ?.removePrefix("Bearer ")
            ?.trim()

        return currentAccessToken != requestToken
    }

    private fun waitForRefreshAndRetry(originalRequest: Request): Request? {
        // 간단한 대기 로직 (실제로는 더 정교한 동기화 필요)
        Thread.sleep(100)
        val newAccessToken = authManager.getAccessToken()
        return if (!newAccessToken.isNullOrBlank()) {
            createRequestWithToken(originalRequest, newAccessToken)
        } else {
            null
        }
    }

    private fun refreshTokenAndCreateRequest(originalRequest: Request, tokens: TokenPair): Request? {
        return try {
            isRefreshing = true
            Log.d("TokenAuthenticator", "토큰 갱신 시작")

            val newTokenResponse = runBlocking {
                authService.refreshToken(tokens.refreshToken)
            }

            if (!newTokenResponse.isSuccessful) {
                val errorCode = newTokenResponse.code()
                Log.w("TokenAuthenticator", "토큰 갱신 실패 - HTTP $errorCode")

                // 401/403인 경우 refresh token도 만료된 것으로 간주
                if (errorCode == 401 || errorCode == 403) {
                    throw TokenExpiredException("Refresh token 만료")
                }
                throw TokenRefreshException("토큰 갱신 HTTP 오류: $errorCode")
            }

            val newTokenData = newTokenResponse.body()
                ?: throw TokenRefreshException("토큰 갱신 응답 body가 null")

            // 새 토큰 저장
            authManager.saveToken(newTokenData.accessToken, newTokenData.refreshToken)
            resetRetryCount()

            Log.d("TokenAuthenticator", "토큰 갱신 성공")

            createRequestWithToken(originalRequest, newTokenData.accessToken)

        } catch (e: Exception) {
            Log.e("TokenAuthenticator", "토큰 갱신 중 오류", e)
            throw e
        } finally {
            isRefreshing = false
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

    private fun handleAuthFailure(reason: String): Request? {
        Log.w("TokenAuthenticator", "인증 실패 처리: $reason")
        authManager.deleteAll()
        resetRetryCount()
        return null
    }

    // 데이터 클래스들
    private data class TokenPair(
        val accessToken: String,
        val refreshToken: String
    )

    // 커스텀 예외들
    private class TokenRefreshException(message: String) : Exception(message)
    private class TokenExpiredException(message: String) : Exception(message)
}
