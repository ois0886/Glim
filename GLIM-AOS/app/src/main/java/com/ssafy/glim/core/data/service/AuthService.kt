package com.ssafy.glim.core.data.service

import com.ssafy.glim.core.data.dto.request.LoginRequest
import com.ssafy.glim.core.data.dto.request.SignUpRequest
import com.ssafy.glim.core.data.dto.response.SignUpResponse
import com.ssafy.glim.core.data.dto.token.AuthToken
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    // 회원가입
    @POST("api/v1/members")
    suspend fun signUp(
        @Body request: SignUpRequest,
    ): SignUpResponse

    // 로그인
    @POST("api/v1/auth/login")
    suspend fun login(
        @Body request: LoginRequest,
    ): AuthToken

    // 리프레시 토큰 재발급
    @POST("api/v1/auth/refresh")
    suspend fun refreshToken(): AuthToken

    // 이메일 인증
    @POST("api/v1/auth/verify-email")
    suspend fun verifyEmail(
        email: String,
    ): Unit

    // 이메일 인증 재전송
    @POST("api/v1/auth/resend-verification")
    suspend fun resendVerificationEmail(
        email: String,
    ): Unit
}
