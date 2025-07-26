package com.ssafy.glim.core.data.repository

import android.util.Log
import com.ssafy.glim.core.common.extensions.toBirthDateList
import com.ssafy.glim.core.data.authmanager.AuthManager
import com.ssafy.glim.core.data.datasource.remote.AuthRemoteDataSource
import com.ssafy.glim.core.data.dto.request.LoginRequest
import com.ssafy.glim.core.data.dto.request.SignUpRequest
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
        birthDate: String,
    ) {
        val birthDateList = birthDate.toBirthDateList()
        val request = SignUpRequest(
            email = email,
            nickname = nickname,
            password = password,
            gender = gender,
            birthDate = birthDateList,
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

        runCatching { dataSource.login(request) }
            .onSuccess {
                authManager.saveToken(
                    accessToken = it.accessToken,
                    refreshToken = it.refreshToken
                )
            }
            .onFailure {
                Log.d("AuthRepositoryImpl", "Login failed: ${it.message}")
            }

    }

    override suspend fun verifyEmail(email: String) {
        dataSource.verifyEmail(email)
    }
}
