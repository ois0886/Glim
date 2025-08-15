package com.ssafy.glim.core.data.datasource.remote

import com.ssafy.glim.core.data.api.AuthApi
import com.ssafy.glim.core.data.dto.request.LoginRequest
import com.ssafy.glim.core.data.dto.request.VerifyEmailRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
    private val service: AuthApi,
) {

    suspend fun signUp(
        request: RequestBody,
        profileImage: MultipartBody.Part?
    ) = service.signUp(
        request = request,
        profileImage = profileImage
    )

    suspend fun login(request: LoginRequest) = service.login(request)

    suspend fun verifyEmail(request: VerifyEmailRequest) = service.verifyEmail(request)
}
