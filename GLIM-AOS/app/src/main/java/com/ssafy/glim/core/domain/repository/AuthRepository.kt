package com.ssafy.glim.core.domain.repository

import com.ssafy.glim.core.data.dto.token.AuthToken

interface AuthRepository {

    suspend fun signUp(
        email: String,
        nickname: String,
        password: String,
        gender: String,
        birthDate: String,
    )

    suspend fun login(
        email: String,
        password: String,
    )

    suspend fun refreshToken(): AuthToken

    suspend fun verifyEmail(email: String)

    suspend fun resendVerificationEmail(email: String)
}
