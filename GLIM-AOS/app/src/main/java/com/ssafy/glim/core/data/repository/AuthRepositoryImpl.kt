package com.ssafy.glim.core.data.repository

import android.util.Log
import com.ssafy.glim.core.data.authmanager.AuthManager
import com.ssafy.glim.core.data.datasource.remote.AuthRemoteDataSource
import com.ssafy.glim.core.data.dto.request.LoginRequest
import com.ssafy.glim.core.data.dto.request.SignUpRequest
import com.ssafy.glim.core.data.dto.request.VerifyEmailRequest
import com.ssafy.glim.core.data.mapper.toDomain
import com.ssafy.glim.core.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val dataSource: AuthRemoteDataSource,
    private val authManager: AuthManager
) : AuthRepository {

    override suspend fun signUp(
        email: String,
        nickname: String,
        password: String,
        gender: String,
        birthDate: List<Int>,
    ) {
        val request = SignUpRequest(
            email = email,
            nickname = nickname,
            password = password,
            gender = gender,
            birthDate = birthDate,
        )

        dataSource.signUp(request)
    }

    override suspend fun login(email: String, password: String) {
        val response = runCatching {
            dataSource.login(LoginRequest(email = email, password = password))
        }.onFailure { exception ->
            Log.d("AuthRepositoryImpl", "login failed: ${exception.message}")
        }.getOrThrow()
        authManager.saveToken(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken
        )
        authManager.saveUserInfo(
            email = response.memberEmail,
            userId = response.memberId.toString()
        )
    }

    override suspend fun verifyEmail(email: String) =
        dataSource.verifyEmail(VerifyEmailRequest(email)).toDomain()

    override suspend fun logOut() {
        authManager.deleteAll()
    }
}
