package com.ssafy.glim.core.domain.repository

import android.graphics.Bitmap
import com.ssafy.glim.core.domain.model.user.User

interface UserRepository {

    suspend fun getUserById(): User

    suspend fun updateUser(
        password: String,
        nickname: String,
        gender: String,
        birthDate: String,
        profileImage: Bitmap
    ): User

    suspend fun deleteUser()

    suspend fun logout()
}
