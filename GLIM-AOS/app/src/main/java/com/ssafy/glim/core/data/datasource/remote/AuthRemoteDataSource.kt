package com.ssafy.glim.core.data.datasource.remote

import com.ssafy.glim.core.data.dto.request.LoginRequest
import com.ssafy.glim.core.data.dto.request.LogoutRequest
import com.ssafy.glim.core.data.dto.request.SignUpRequest
import com.ssafy.glim.core.data.service.AuthService
import javax.inject.Inject

class AuthRemoteDataSource
    @Inject
    constructor(
        private val service: AuthService,
    ) {
        suspend fun signUp(request: SignUpRequest) = service.signUp(request)

        suspend fun login(request: LoginRequest) = service.login(request)

        suspend fun refreshToken(authorization: String) = service.refreshToken(authorization)

        suspend fun logout(request: LogoutRequest) = service.logout(request)
    }
