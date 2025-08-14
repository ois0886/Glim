package com.ssafy.glim.core.data.datasource.remote

import com.ssafy.glim.core.data.dto.request.LoginRequest
import com.ssafy.glim.core.data.dto.request.SignUpRequest
import com.ssafy.glim.core.data.dto.request.VerifyEmailRequest
import com.ssafy.glim.core.data.api.AuthApi
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
    private val service: AuthApi,
) {

    suspend fun signUp(request: SignUpRequest) = service.signUp(request)

    suspend fun login(request: LoginRequest) = service.login(request)

    suspend fun verifyEmail(request: VerifyEmailRequest) = service.verifyEmail(request)
}
