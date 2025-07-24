package com.ssafy.glim.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(
        email: String,
        password: String,
    ): Flow<Result<Unit>>

    fun signUp(
        email: String,
        nickname: String,
        password: String,
        gender: String,
        birthDate: String,
    ): Flow<Result<Unit>>

    fun sendVerificationCode(code: String): Flow<Result<Unit>>
}
