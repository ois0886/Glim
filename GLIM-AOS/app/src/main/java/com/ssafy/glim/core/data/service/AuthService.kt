package com.ssafy.glim.core.data.service

import com.ssafy.glim.core.data.dto.request.LoginRequest
import com.ssafy.glim.core.data.dto.request.LogoutRequest
import com.ssafy.glim.core.data.dto.request.SignUpRequest
import com.ssafy.glim.core.data.dto.response.LoginResponse
import com.ssafy.glim.core.data.dto.response.LogoutResponse
import com.ssafy.glim.core.data.dto.response.RefreshTokenResponse
import com.ssafy.glim.core.data.dto.response.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {

    @POST("api/v1/members")
    suspend fun signUp(
        @Body request: SignUpRequest
    ): Response<SignUpResponse>

    @POST("api/v1/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("api/v1/auth/refresh")
    suspend fun refreshToken(
        @Header("Authorization") authorization: String
    ): Response<RefreshTokenResponse>

    @POST("api/v1/auth/logout")
    suspend fun logout(
        @Body request: LogoutRequest
    ): Response<LogoutResponse>
}