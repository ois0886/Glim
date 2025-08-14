package com.ssafy.glim.core.data.api

import com.ssafy.glim.core.data.dto.request.LoginRequest
import com.ssafy.glim.core.data.dto.request.VerifyEmailRequest
import com.ssafy.glim.core.data.dto.response.SignUpResponse
import com.ssafy.glim.core.data.dto.response.VerifyEmailResponse
import com.ssafy.glim.core.data.dto.token.AuthToken
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AuthApi {

    // 회원가입
    @Multipart
    @POST("api/v1/members")
    suspend fun signUp(
        @Part("signUpRequest") request: RequestBody,
        @Part profileImage: MultipartBody.Part?
    ): SignUpResponse

    // 로그인
    @POST("api/v1/auth/login")
    suspend fun login(
        @Body request: LoginRequest,
    ): AuthToken

    // 액섹스 토큰 갱신
    @POST("api/v1/auth/refresh")
    suspend fun refreshToken(
        @Header("Authorization") refreshToken: String
    ): Response<AuthToken>

    // 이메일 인증
    @POST("api/v1/auth/email-verification-code")
    suspend fun verifyEmail(
        @Body request: VerifyEmailRequest
    ): VerifyEmailResponse
}
