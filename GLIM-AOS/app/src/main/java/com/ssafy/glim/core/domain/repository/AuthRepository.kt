package com.ssafy.glim.core.domain.repository

import android.graphics.Bitmap
import com.ssafy.glim.core.domain.model.VerifyEmail

interface AuthRepository {

    suspend fun signUp(
        email: String,
        nickname: String,
        password: String,
        gender: String,
        birthDate: List<Int>,
        profileBitmap: Bitmap
    )

    suspend fun login(
        email: String,
        password: String,
    )

    suspend fun verifyEmail(email: String): VerifyEmail

    suspend fun refreshFcmToken(newToken: String): Result<Unit>
}
