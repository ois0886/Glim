package com.example.myapplication.core.data.repository

import com.example.myapplication.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl
    @Inject
    constructor() : AuthRepository {
        override fun login(): Flow<Result<Unit>> {
            TODO("Not yet implemented")
        }

        override fun signUp(): Flow<Result<Unit>> {
            TODO("Not yet implemented")
        }

        override fun sendVerificationCode(code: String): Flow<Result<Unit>> {
            TODO("Not yet implemented")
        }
    }
