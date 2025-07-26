package com.ssafy.glim.core.domain.repository

import com.ssafy.glim.core.domain.model.user.User

interface UserRepository {

    suspend fun getUserByEmail(email: String): User

    suspend fun updateUser(
        memberId: Long,
        password: String,
        nickname: String,
        gender: String,
        birthDate: List<Int>,
    ): User

    suspend fun deleteUser(memberId: Long): User
}
