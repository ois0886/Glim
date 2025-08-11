package com.ssafy.glim.core.domain.repository

interface FcmRepository {
    suspend fun registerToken(
        deviceToken: String,
        deviceType: String,
        deviceId: String
    )

    suspend fun deleteToken(
        deviceId: String
    )
}
