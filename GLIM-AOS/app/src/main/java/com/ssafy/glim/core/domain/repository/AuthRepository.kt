package com.ssafy.glim.core.domain.repository

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

    suspend fun refreshToken()

    suspend fun verifyEmail(email: String)

    suspend fun resendVerificationEmail(email: String)
}
