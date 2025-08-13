package com.ssafy.glim.core.domain.repository

import android.graphics.Bitmap
import com.ssafy.glim.core.domain.model.user.User

interface UserRepository {

    suspend fun getUserById(): User

    suspend fun updateUser(
        memberId: Long,
        password: String,
        nickname: String,
        gender: String,
        birthDate: List<Int>,
        profileUrl: Bitmap?
    ): User

    suspend fun deleteUser()

    suspend fun logout()
}
