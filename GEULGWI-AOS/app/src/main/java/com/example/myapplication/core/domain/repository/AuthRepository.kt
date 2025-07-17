package com.example.myapplication.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(): Flow<Result<Unit>>

    fun signUp(): Flow<Result<Unit>>

    fun sendVerificationCode(code: String): Flow<Result<Unit>>
}
