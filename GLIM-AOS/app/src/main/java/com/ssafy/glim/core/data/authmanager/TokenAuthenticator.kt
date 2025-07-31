package com.ssafy.glim.core.data.authmanager

import android.util.Log
import com.ssafy.glim.core.data.dto.token.AuthToken
import com.ssafy.glim.core.data.service.AuthService
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val authManager: AuthManager,
    private val authService: AuthService
) : Authenticator {

    // Thread-safe한 카운터 사용
    private val retryCount = AtomicInteger(0)
    private val maxRetryCount = 3

    // 토큰 갱신을 위한 Mutex (코루틴 동기화)
    private val tokenRefreshMutex = Mutex()

    // 토큰 갱신 상태를 관리하는 AtomicReference
    private val refreshState = AtomicReference<RefreshState>(RefreshState.Idle)

    // 토큰 갱신 대기를 위한 CountDownLatch
    @Volatile
    private var refreshLatch: CountDownLatch? = null

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

            // 토큰이 이미 갱신되었는지 확인
            if (isTokenAlreadyRefreshed(response, currentTokens.accessToken)) {
                Log.d("TokenAuthenticator", "토큰이 이미 갱신됨, 새 토큰으로 재시도")
                resetRetryCount()
                return createRequestWithToken(response.request, currentTokens.accessToken)
            }

            // 토큰 갱신 처리
            handleTokenRefresh(response.request, currentTokens)
        } catch (e: Exception) {
            Log.e("TokenAuthenticator", "토큰 인증 처리 중 오류", e)
            handleAuthFailure("토큰 인증 오류: ${e.message}")
            null
        }
    }

    private fun handleTokenRefresh(originalRequest: Request, tokens: TokenPair): Request? {
        return when (val currentState = refreshState.get()) {
            is RefreshState.Idle -> {
                // 토큰 갱신 시작
                if (refreshState.compareAndSet(RefreshState.Idle, RefreshState.InProgress)) {
                    refreshLatch = CountDownLatch(1)
                    refreshTokenAndCreateRequest(originalRequest, tokens)
                } else {
                    // 다른 스레드가 갱신을 시작했으므로 대기
                    waitForRefreshAndRetry(originalRequest)
                }
            }

            is RefreshState.InProgress -> {
                // 이미 갱신 중이므로 대기
                waitForRefreshAndRetry(originalRequest)
            }

            is RefreshState.Success -> {
                // 갱신 성공했으므로 새 토큰으로 요청
                val newToken = authManager.getAccessToken()
                if (!newToken.isNullOrBlank()) {
                    resetRetryCount()
                    createRequestWithToken(originalRequest, newToken)
                } else {
                    handleAuthFailure("갱신된 토큰을 찾을 수 없음")
                    null
                }
            }

            is RefreshState.Failed -> {
                // 갱신 실패했으므로 인증 실패 처리
                handleAuthFailure("토큰 갱신 실패: ${currentState.error}")
                null
            }
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
        return try {
            Log.d("TokenAuthenticator", "토큰 갱신 완료 대기 중...")

            // 토큰 갱신 완료를 최대 10초까지 대기
            val latch = refreshLatch
            val refreshCompleted = latch?.await(10, TimeUnit.SECONDS) ?: false

            if (!refreshCompleted) {
                Log.w("TokenAuthenticator", "토큰 갱신 대기 시간 초과")
                handleAuthFailure("토큰 갱신 대기 시간 초과")
                return null
            }

            when (val state = refreshState.get()) {
                is RefreshState.Success -> {
                    Log.d("TokenAuthenticator", "토큰 갱신 완료, 새 토큰으로 재시도")
                    val newAccessToken = authManager.getAccessToken()
                    if (!newAccessToken.isNullOrBlank()) {
                        resetRetryCount()
                        createRequestWithToken(originalRequest, newAccessToken)
                    } else {
                        handleAuthFailure("갱신된 토큰을 찾을 수 없음")
                        null
                    }
                }

                is RefreshState.Failed -> {
                    Log.w("TokenAuthenticator", "토큰 갱신 실패: ${state.error}")
                    handleAuthFailure("토큰 갱신 실패: ${state.error}")
                    null
                }

                else -> {
                    Log.w("TokenAuthenticator", "예상치 못한 갱신 상태: $state")
                    handleAuthFailure("예상치 못한 갱신 상태")
                    null
                }
            }
        } catch (e: InterruptedException) {
            Log.w("TokenAuthenticator", "토큰 갱신 대기 중 인터럽트 발생", e)
            Thread.currentThread().interrupt()
            handleAuthFailure("토큰 갱신 대기 인터럽트")
            null
        }
    }

    private fun refreshTokenAndCreateRequest(originalRequest: Request, tokens: TokenPair): Request? {
        return try {
            Log.d("TokenAuthenticator", "토큰 갱신 시작")

            // 코루틴으로 토큰 갱신 수행
            val result = runBlocking {
                tokenRefreshMutex.withLock {
                    performTokenRefresh(tokens.refreshToken)
                }
            }

            when (result) {
                is RefreshResult.Success -> {
                    // 새 토큰 저장 (accessToken과 refreshToken 모두 갱신)
                    authManager.saveToken(result.tokenData.accessToken, result.tokenData.refreshToken)

                    // 성공 상태로 변경하고 대기 중인 스레드들에게 알림
                    refreshState.set(RefreshState.Success)
                    refreshLatch?.countDown()
                    resetRetryCount()

                    Log.d("TokenAuthenticator", "토큰 갱신 성공")
                    createRequestWithToken(originalRequest, result.tokenData.accessToken)
                }

                is RefreshResult.Failure -> {
                    // 실패 상태로 변경하고 대기 중인 스레드들에게 알림
                    refreshState.set(RefreshState.Failed(result.error))
                    refreshLatch?.countDown()

                    Log.e("TokenAuthenticator", "토큰 갱신 실패: ${result.error}")
                    handleAuthFailure("토큰 갱신 실패: ${result.error}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("TokenAuthenticator", "토큰 갱신 중 오류", e)

            // 실패 상태로 변경하고 대기 중인 스레드들에게 알림
            refreshState.set(RefreshState.Failed(e.message ?: "알 수 없는 오류"))
            refreshLatch?.countDown()

            handleAuthFailure("토큰 갱신 오류: ${e.message}")
            null
        } finally {
            // 5초 후 상태를 Idle로 리셋 (새로운 갱신 요청을 위해)
            scheduleStateReset()
        }
    }

    private fun performTokenRefresh(refreshToken: String): RefreshResult {
        return try {
            // Header에 Bearer 토큰 형식으로 전송
            val authHeader = "Bearer $refreshToken"
            val response = authService.refreshToken(authHeader)

            if (!response.isSuccessful) {
                val errorCode = response.code()
                Log.w("TokenAuthenticator", "토큰 갱신 실패 - HTTP $errorCode")

                // 401/403인 경우 refresh token도 만료된 것으로 간주
                return if (errorCode == 401 || errorCode == 403) {
                    RefreshResult.Failure("Refresh token 만료 (HTTP $errorCode)")
                } else {
                    RefreshResult.Failure("토큰 갱신 HTTP 오류: $errorCode")
                }
            }

            val tokenData = response.body()
                ?: return RefreshResult.Failure("토큰 갱신 응답 body가 null")

            RefreshResult.Success(tokenData)
        } catch (e: Exception) {
            RefreshResult.Failure("토큰 갱신 네트워크 오류: ${e.message}")
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

        // 상태를 실패로 변경하고 대기 중인 스레드들에게 알림
        refreshState.set(RefreshState.Failed(reason))
        refreshLatch?.countDown()

        return null
    }

    private fun scheduleStateReset() {
        // 별도 스레드에서 5초 후 상태 리셋
        Thread {
            try {
                Thread.sleep(5000)
                if (refreshState.get() !is RefreshState.InProgress) {
                    refreshState.set(RefreshState.Idle)
                    refreshLatch = null
                    Log.d("TokenAuthenticator", "토큰 갱신 상태 리셋 완료")
                }
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
                Log.d("TokenAuthenticator", "상태 리셋 스레드 인터럽트됨")
            }
        }.start()
    }

    // 데이터 클래스들
    private data class TokenPair(
        val accessToken: String,
        val refreshToken: String
    )

    // 토큰 갱신 상태를 나타내는 sealed class
    private sealed class RefreshState {
        object Idle : RefreshState()
        object InProgress : RefreshState()
        object Success : RefreshState()
        data class Failed(val error: String) : RefreshState()
    }

    // 토큰 갱신 결과를 나타내는 sealed class
    private sealed class RefreshResult {
        data class Success(val tokenData: AuthToken) : RefreshResult()
        data class Failure(val error: String) : RefreshResult()
    }
}
