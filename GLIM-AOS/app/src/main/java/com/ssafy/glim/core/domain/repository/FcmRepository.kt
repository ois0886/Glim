package com.ssafy.glim.core.domain.repository

interface FcmRepository {
    suspend fun registerToken(): Result<Unit>

    suspend fun refreshToken(
        deviceToken: String
    ): Result<Unit>

    suspend fun deleteToken(): Result<Unit>
}
