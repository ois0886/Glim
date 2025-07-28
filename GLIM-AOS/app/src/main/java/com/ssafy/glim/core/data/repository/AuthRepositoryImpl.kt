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

    override suspend fun login(
        email: String,
        password: String,
    ) {
        val request = LoginRequest(
            email = email,
            password = password,
        )

        // TODO: refreshToken 추가
        runCatching { dataSource.login(request) }
            .onSuccess {
                Log.d("AuthRepositoryImpl", "API success")
                try {
                    authManager.saveToken(
                        accessToken = it.accessToken
                    )
                    Log.d("AuthRepositoryImpl", "Token save success")
                } catch (e: Exception) {
                    Log.e("AuthRepositoryImpl", "Token save failed: ${e.message}")
                    throw e
                }
            }
            .onFailure {
                Log.d("AuthRepositoryImpl", "Login failed: ${it.message}")
            }
    }

    override suspend fun verifyEmail(email: String) =
        dataSource.verifyEmail(VerifyEmailRequest(email)).toDomain()
}
