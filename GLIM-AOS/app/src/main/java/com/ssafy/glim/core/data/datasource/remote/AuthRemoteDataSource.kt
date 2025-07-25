package com.ssafy.glim.core.data.datasource.remote

import com.ssafy.glim.core.data.dto.request.LoginRequest
import com.ssafy.glim.core.data.dto.request.SignUpRequest
import com.ssafy.glim.core.data.service.AuthService
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
    private val service: AuthService,
) {

    suspend fun signUp(request: SignUpRequest) = service.signUp(request)

    suspend fun login(request: LoginRequest) = service.login(request)

    suspend fun verifyEmail(email: String) = service.verifyEmail(email)

    suspend fun resendVerificationEmail(email: String) = service.resendVerificationEmail(email)
}
